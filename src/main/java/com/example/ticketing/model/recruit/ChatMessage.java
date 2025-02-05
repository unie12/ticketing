package com.example.ticketing.model.recruit;

import com.example.ticketing.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
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
    @JoinColumn(name = "recruitment_post_id", nullable = false)
    private RecruitmentPost recruitmentPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User sender;

    private String messageContent;
    private LocalDateTime timestamp;

    public ChatMessage(RecruitmentPost recruitmentPost, User sender, String messageContent) {
        this.recruitmentPost = recruitmentPost;
        this.sender = sender;
        this.messageContent = messageContent;
        this.timestamp = LocalDateTime.now();
    }
}
