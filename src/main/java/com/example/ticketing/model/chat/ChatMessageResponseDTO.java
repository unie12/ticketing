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
    private String senderName;
    private String content;
    private LocalDateTime timeStamp;

    public static ChatMessageResponseDTO from(ChatMessage message) {
        return new ChatMessageResponseDTO(
                message.getSender().getUsername(),
                message.getMessageContent(),
                message.getTimestamp()
        );
    }

}
