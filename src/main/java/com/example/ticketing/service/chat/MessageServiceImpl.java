package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.ChatMessage;
import com.example.ticketing.model.chat.ChatMessageResponseDTO;
import com.example.ticketing.model.chat.ChatNotificationDTO;
import com.example.ticketing.model.chat.ChatRoom;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.chat.ChatMessageRepository;
import com.example.ticketing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
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

        Set<Long> participants = chatRoomService.getRoomParticipants(roomId);
        for (Long participantId : participants) {
            if (!participantId.equals(senderId)) {
                handleMessageForParticipant(roomId, participantId, responseDTO);
            }
        }

        return responseDTO;

//        messagingTemplate.convertAndSend(
//                "/topic/chat/" + roomId,
//                new ChatMessageResponseDTO(sender.getUsername(), content, LocalDateTime.now())
//        );
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
    public List<ChatMessageResponseDTO> getMessages(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
        return chatMessageRepository.findByChatRoom_Id(roomId, pageable).getContent()
                .stream().map(ChatMessageResponseDTO::from)
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
