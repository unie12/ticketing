package com.example.ticketing.model.chat;

import com.example.ticketing.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_room_id", nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User sender;

    private String messageContent;
    private LocalDateTime timestamp;

    @Builder
    public ChatMessage(ChatRoom chatRoom, User sender, String messageContent, LocalDateTime timestamp) {
        this.chatRoom = chatRoom;
        this.sender = sender;
        this.messageContent = messageContent;
        this.timestamp = timestamp;
    }

}
