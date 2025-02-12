package com.example.ticketing.service.chat;

import com.example.ticketing.model.chat.ChatMessageEvent;
import com.example.ticketing.model.chat.ChatNotificationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {
    private final ChatPresenceService chatPresenceService;
    private final ChatRoomService chatRoomService;

    @KafkaListener(
            topics = "chat-message-topic",
            groupId = "notification-group",
            containerFactory = "chatMessageListenerContainerFactory"
    )
    public void sendNotification(ConsumerRecord<String, ChatMessageEvent> record) {
        ChatMessageEvent event = record.value();
        log.info("Sending notification for message:{}", event);

        Set<Long> participantIds = chatRoomService.getRoomParticipants(event.getRoomId());
        for (Long participantId : participantIds) {
            if (!participantId.equals(event.getSenderId())) { // 작성자는 제외
                String userStatus = chatPresenceService.getUserStatus(participantId);

                if (!"ONLINE".equals(userStatus)) { // 오프라인 사용자에게만 알림 전송
                    ChatNotificationDTO notification = ChatNotificationDTO.builder()
                            .type("NEW_MESSAGE")
                            .roomId(event.getRoomId())
                            .senderId(event.getSenderId())
                            .content(event.getContent())
                            .timestamp(event.getTimestamp())
                            .build();

                    chatPresenceService.sendNotification(participantId, notification);
                }
            }
        }
    }
}
