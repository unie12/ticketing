package com.example.ticketing.model.review;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class ReviewResponse {
    private Long id;
    private String content;
    private Integer rating;
    private String userName;
    private String storeName;
    private String storeId;
    private LocalDateTime visitDateTime;
    private Crowdedness crowdedness;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int heartCount;
    private boolean isLiked;

    public static ReviewResponse from(Review review, boolean isLiked) {
        return ReviewResponse.builder()
                .id(review.getId())
                .content(review.getContent())
                .rating(review.getRating())
                .userName(review.getUser().getUsername())
                .storeName(review.getStore().getPlaceName())
                .storeId(review.getStore().getId())
                .visitDateTime(review.getVisitInfo() != null ? review.getVisitInfo().getVisitDateTime() : null)
                .crowdedness(review.getVisitInfo() != null ? review.getVisitInfo().getCrowdedness() : null)
                .imageUrls(review.getImages().stream().map(ReviewImage::getImageUrl).collect(Collectors.toList()))
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .heartCount(review.getHeartCount())
                .isLiked(isLiked)
                .build();
    }
}
