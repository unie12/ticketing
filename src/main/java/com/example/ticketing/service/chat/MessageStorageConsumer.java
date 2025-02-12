package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.ChatMessage;
import com.example.ticketing.model.chat.ChatMessageEvent;
import com.example.ticketing.model.chat.ChatRoom;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.chat.ChatMessageRepository;
import com.example.ticketing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageStorageConsumer {
    private final ChatMessageRepository chatMessageRepository;

    private final ChatRoomService chatRoomService;
    private final UserService userService;

    @KafkaListener(
            topics = "chat-message-topic",
            groupId = "message-storage-group",
            containerFactory = "chatMessageListenerContainerFactory"
    )
    public void consumeChatMessage(ConsumerRecord<String, ChatMessageEvent> record) {
        ChatMessageEvent event = record.value();
        log.info("Consumed chat message: {}", event);

        // 1. 메시지 db 저장
        ChatRoom chatRoom = chatRoomService.findChatRoomById(event.getRoomId());
        User sender = userService.findUserById(event.getSenderId());

        ChatMessage chatMessage = new ChatMessage(chatRoom, sender, event.getContent(), event.getTimestamp());
        ChatMessage save = chatMessageRepository.save(chatMessage);

        event.setId(save.getId());
    }
}
