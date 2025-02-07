package com.example.ticketing.model.recruit;

import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.exception.RecruitmentException;
import com.example.ticketing.model.chat.ChatRoom;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RecruitmentPost {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private int maxParticipants;

    @Column(nullable = false)
    private int currentParticipants;

    @Column(nullable = false)
    private LocalDateTime meetingTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RecruitmentStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime lastModifiedAt;

    /**
     * 연관관계 매핑
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "recruitmentPost", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Participant> participants = new ArrayList<>();

    @OneToOne(mappedBy = "recruitmentPost", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private ChatRoom chatRoom;

    @Builder
    public RecruitmentPost(Store store, User author, String title, String content, int maxParticipants, LocalDateTime meetingTime) {
        this.store = store;
        this.author = author;
        this.title = title;
        this.content = content;
        this.maxParticipants = maxParticipants;
        this.meetingTime = meetingTime;
        this.currentParticipants = 0;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        lastModifiedAt = LocalDateTime.now();
        status = RecruitmentStatus.OPEN;
    }

    public boolean isFull() {
        return currentParticipants >= maxParticipants;
    }

    public boolean isAuthor(User user) {
        return this.author.equals(user);
    }

    public void close() {
        if (this.status == RecruitmentStatus.OPEN) {
            this.status = RecruitmentStatus.CLOSED;
        }
    }

    public void update(RecruitmentRequest request) {
        validateUpdate(request);

        this.title = request.getTitle();
        this.content = request.getContent();
        this.maxParticipants = request.getMaxParticipants();;
        this.meetingTime = request.getMeetingTime();
        this.lastModifiedAt = LocalDateTime.now();
    }

    public boolean canJoin() {
        return this.status == RecruitmentStatus.OPEN && !isFull();
    }

    public void incrementParticipants() {
        this.currentParticipants++;
    }

    public void decrementParticipants() {
        this.currentParticipants = Math.max(0, this.currentParticipants - 1);
    }

    private void validateUpdate(RecruitmentRequest request) {
        if (request.getMaxParticipants() < this.currentParticipants) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_INVALID_MAX_PARTICIPANTS);
        }

        if (request.getMeetingTime().isBefore(LocalDateTime.now())) {
            throw new RecruitmentException(ErrorCode.RECRUITMENTPOST_INVALID_MEETING_TIME);
        }
    }
    public void addParticipant(Participant participant) {
        this.participants.add(participant);
        incrementParticipants();
    }
}
