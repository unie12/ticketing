package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.ChatMessageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UnreadCountConsumer {
    private final ChatRoomService chatRoomService;
    private final ChatPresenceService chatPresenceService;

    @KafkaListener(
            topics = "chat-message-topic",
            groupId = "unread-count-group",
            containerFactory = "chatMessageListenerContainerFactory"
    )
    public void storeMessage(ConsumerRecord<String, ChatMessageEvent> record) {
        ChatMessageEvent event = record.value();
        log.info("Updating unread count for message:{}", event);

        Set<Long> participantIds = chatRoomService.getRoomParticipants(event.getRoomId());
        for (Long participantId : participantIds) {
            if (!participantId.equals(event.getSenderId())) {
                String userStatus = chatPresenceService.getUserStatus(participantId);
                Long activeRoom = chatPresenceService.getActiveRoom(participantId);

                if (!"ONLINE".equals(userStatus) ||
                activeRoom == null || !activeRoom.equals(event.getRoomId())) {
                    chatPresenceService.incrementUnreadCount(event.getRoomId(), participantId);
                }
            }
        }
    }
}
