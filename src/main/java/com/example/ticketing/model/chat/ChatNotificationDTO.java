package com.example.ticketing.model.chat;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ChatNotificationDTO {
    private String type;
    private Long roomId;
    private Long senderId;
    private String content;
    private LocalDateTime timestamp;
}
