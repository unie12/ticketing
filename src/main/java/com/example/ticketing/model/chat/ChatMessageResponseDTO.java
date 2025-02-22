package com.example.ticketing.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageResponseDTO {
    private Long id;
    private Long roomId;
    private Long senderId;
    private String senderName;
    private String content;
    private LocalDateTime timeStamp;

    public static ChatMessageResponseDTO from(ChatMessage message) {
        return new ChatMessageResponseDTO(
                message.getId(),
                message.getChatRoom().getId(),
                message.getSender().getId(),
                message.getSender().getUsername(),
                message.getMessageContent(),
                message.getTimestamp()
        );
    }

}
