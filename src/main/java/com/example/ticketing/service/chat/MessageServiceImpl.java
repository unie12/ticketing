package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.*;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.chat.ChatMessageRepository;
import com.example.ticketing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;
    private final ChatPresenceService presenceService;

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public ChatMessageResponseDTO sendMessage(Long roomId, Long senderId, String content) {
        ChatMessage message = saveMessage(roomId, senderId, content);
        ChatMessageResponseDTO responseDTO = ChatMessageResponseDTO.from(message);

        CompletableFuture.runAsync(() -> {
            try {
                handleParticipantNotifications(roomId, senderId, responseDTO);
            } catch (Exception e) {
                log.error("Error in handleParticipantNotifications: {}", e.getMessage());
            }
        });
        return responseDTO;
    }

    private void handleParticipantNotifications(Long roomId, Long senderId, ChatMessageResponseDTO responseDTO) {
        Set<Long> participants = chatRoomService.getRoomParticipants(roomId);
        log.info("Participants for room {}: {}", roomId, participants);

        if (participants.isEmpty()) {
            log.warn("No participants found for room {}", roomId);
            return;
        }
        participants.stream()
                .filter(participantId -> !participantId.equals(senderId))
                .forEach(participantId -> {
                    Long activeRoom = presenceService.getActiveRoom(participantId);
                    String userStatus = presenceService.getUserStatus(participantId);

                    log.info("Checking unread count increment for room {} and user {}", roomId, participantId);
                    // 사용자가 오프라인이거나 다른 채팅방을 보고 있는 경우
                    if (!"ONLINE".equals(userStatus) ||
                            activeRoom == null ||
                            !activeRoom.equals(roomId)) {
                        log.info("Incrementing unread count for room {} and user {}", roomId, participantId);
                        presenceService.incrementUnreadCount(roomId, participantId);
                        sendNotification(participantId, responseDTO);
                        log.info("Sent notification to user {} for room {}", participantId, roomId);
                    } else {
                        log.info("No unread count increment needed for room {} and user {}", roomId, participantId);
                    }
                });
    }
    private void handleMessageForParticipant(Long roomId, Long userId, ChatMessageResponseDTO message) {
        Long activeRoom = presenceService.getActiveRoom(userId);

        if (activeRoom == null || !activeRoom.equals(roomId)) {
            presenceService.incrementUnreadCount(roomId, userId);
            sendNotification(userId, message);
        }
    }

    private void sendNotification(Long userId, ChatMessageResponseDTO message) {
        ChatNotificationDTO notification = ChatNotificationDTO.builder()
                .type("NEW_MESSAGE")
                .roomId(message.getRoomId())
                .senderId(message.getSenderId())
                .content(message.getContent())
                .timestamp(message.getTimeStamp())
                .build();

        messagingTemplate.convertAndSendToUser(
                userId.toString(),
                "/queue/notifications",
                notification
        );
    }

    @Override
    public List<ChatMessageEvent> getMessages(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
        return chatMessageRepository.findByChatRoom_Id(roomId, pageable).getContent()
                .stream().map(ChatMessageEvent::from)
                .collect(Collectors.toList());
    }

    private ChatMessage saveMessage(Long roomId, Long senderId, String content) {
        ChatRoom chatRoom = chatRoomService.findChatRoomById(roomId);
        User sender = userService.findUserById(senderId);

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .messageContent(content)
                .timestamp(LocalDateTime.now())
                .build();

        return chatMessageRepository.save(message);
    }

}
