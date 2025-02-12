package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatMessageProducer {
    private final KafkaTemplate<String, ChatMessageEvent> kafkaTemplate;

    public void sendChatMessage(ChatMessageEvent chatMessageEvent) {
        kafkaTemplate.send("chat-message-topic", chatMessageEvent.getRoomId().toString(), chatMessageEvent);
        log.info("Chat message sent to topic 'chat-message-topic': {}", chatMessageEvent);
    }
}
