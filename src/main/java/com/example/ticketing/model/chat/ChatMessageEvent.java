package com.example.ticketing.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatMessageEvent {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timestamp;

    public static ChatMessageEvent from(ChatMessage message) {
        return new ChatMessageEvent(
                message.getId(),
                message.getChatRoom().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getMessageContent(),
                message.getTimestamp()
        );
    }

    public void setId(Long tmpId) {
        this.id = tmpId;
    }
}

