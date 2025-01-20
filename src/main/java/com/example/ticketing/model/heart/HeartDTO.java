package com.example.ticketing.model.heart;

import com.example.ticketing.model.review.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class HeartDTO {
    private Long id;

    private Long reviewId;
    private Integer rating;

    private String placeName;
    private String storeId;

    private String username;

    private int heartCount;
    private LocalDateTime createdAt;

    public static HeartDTO from(Heart heart) {
        Review review = heart.getReview();
        return HeartDTO.builder()
                .id(heart.getId())
                .reviewId(review.getId())
                .placeName(review.getStore().getPlaceName())
                .storeId(review.getStore().getId())
                .username(review.getUser().getUsername())
                .rating(review.getRating())
                .heartCount(review.getHeartCount())
                .createdAt(heart.getCreatedAt())
                .build();
    }

}
