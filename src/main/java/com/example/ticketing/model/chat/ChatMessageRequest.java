package com.example.ticketing.model.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageRequest {
    private Long chatRoomId;
    private Long senderId;
    private String content;
}
