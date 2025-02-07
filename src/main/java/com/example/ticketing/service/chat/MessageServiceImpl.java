package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.ChatMessage;
import com.example.ticketing.model.chat.ChatMessageResponseDTO;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomService chatRoomService;
    private final UserService userService;

    private SimpMessagingTemplate messagingTemplate;

    @Override
    public void sendMessage(Long roomId, Long senderId, String content) {
        ChatRoom chatRoom = chatRoomService.findChatRoomById(roomId);
        User sender = userService.findUserById(senderId);

        ChatMessage message = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .messageContent(content)
                .timestamp(LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);

        messagingTemplate.convertAndSend(
                "/topic/chat/" + roomId,
                new ChatMessageResponseDTO(sender.getUsername(), content, LocalDateTime.now())
        );
    }

    @Override
    public List<ChatMessageResponseDTO> getMessages(Long roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
        return chatMessageRepository.findByChatRoom_Id(roomId, pageable).getContent()
                .stream().map(ChatMessageResponseDTO::from)
                .collect(Collectors.toList());
    }
}
