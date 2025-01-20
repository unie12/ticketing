package com.example.ticketing.model.review;

import com.example.ticketing.model.heart.Heart;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @Column(nullable = true)
    private Integer rating;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(nullable = false)
    private int heartCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "visit_info_id")
    private VisitInfo visitInfo;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Heart> hearts = new ArrayList<>();


    @Builder
    public Review(User user, Store store, String content, Integer rating, VisitInfo visitInfo) {
        this.user = user;
        this.store = store;
        this.content = content;
        this.rating = rating;
        setVisitInfo(visitInfo);
        this.updatedAt = this.createdAt;
    }

    public void updateContent(String content) {
        this.content = content;
    }

    public void updateRating(int rating) {
        this.rating = rating;
    }

    public void addImage(ReviewImage reviewImage) {
        this.images.add(reviewImage);
        reviewImage.setReview(this);
    }

    public void setVisitInfo(VisitInfo visitInfo) {
        this.visitInfo = visitInfo;
        visitInfo.setReview(this);
    }

    public void incrementHeartCount() {
        this.heartCount++;
    }

    public void decrementHeartCount() {
        this.heartCount = Math.max(0, this.heartCount - 1);
    }
}