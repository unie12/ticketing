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
public class Participant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recruitment_post_id", nullable = false)
    private RecruitmentPost recruitmentPost;

    private LocalDateTime joinedAt;

    public Participant(User user, RecruitmentPost recruitmentPost) {
        this.user = user;
        this.recruitmentPost = recruitmentPost;
        this.joinedAt = LocalDateTime.now();
    }
}
