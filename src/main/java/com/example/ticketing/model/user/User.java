package com.example.ticketing.model.user;

import com.example.ticketing.model.auth.AuthProvider;
import com.example.ticketing.model.chat.ChatMessage;
import com.example.ticketing.model.chat.ChatRoomParticipant;
import com.example.ticketing.model.event.Reservation;
import com.example.ticketing.model.favorite.Favorite;
import com.example.ticketing.model.heart.Heart;
import com.example.ticketing.model.recruit.Participant;
import com.example.ticketing.model.recruit.RecruitmentPost;
import com.example.ticketing.model.review.Review;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
// 양방향 관계 무한 순환 참조 에러 방지
@ToString(exclude = {
        "reservations", "userCoupons", "favorites", "hearts", "recruitmentPosts",
        "participants", "messages","chatRoomParticipants"
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 기본 정보
     */
    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    private String imgUrl;
    private boolean emailVerified = false;

    @Column(name = "verification_token")
    private String verificationToken;

    @Column(name = "verification_token_expiry")
    private LocalDateTime verificationTokenExpiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider provider = AuthProvider.LOCAL;

//    private String providerId;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_USER;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    // 연령, 학과 등

    /**
     * 연관관계 매핑
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Heart> hearts = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = false) // 사용자 삭제되더라도 구인글 데이터는 남도록?
    private List<RecruitmentPost> recruitmentPosts = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = false)
    private List<Participant> participants = new ArrayList<>();

    @OneToMany(mappedBy = "sender", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatMessage> messages = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChatRoomParticipant> chatRoomParticipants = new ArrayList<>();

//    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<ChatMessageParticipant> messageParticipants = new ArrayList<>();

    @Builder
    public User(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void verifyEmail() {
        this.emailVerified = true;
        this.verificationToken = null;
        this.verificationTokenExpiry = null;
    }

    public void generateVerificationToken() {
        this.verificationToken = UUID.randomUUID().toString();
        this.verificationTokenExpiry = LocalDateTime.now().plusHours(24);
    }

    public boolean hasLikedReview(Review review) {
        return this.hearts.stream()
                .anyMatch(heart -> heart.getReview().equals(review));
    }

    public boolean hasJoinedRecruitment(RecruitmentPost post) {
        return participants.stream()
                .anyMatch(p -> p.getRecruitmentPost().equals(post));
    }

}
