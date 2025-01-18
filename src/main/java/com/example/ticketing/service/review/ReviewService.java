package com.example.ticketing.service.review;

import com.example.ticketing.model.review.ReviewRequest;
import com.example.ticketing.model.review.ReviewResponse;

import java.util.List;

public interface ReviewService {
    ReviewResponse writeReview(ReviewRequest request, Long userID, String storeId);
    ReviewResponse modifyReview(Long reviewId, ReviewRequest request, Long userID);
    ReviewResponse deleteReview(Long reviewId, Long userId);
    List<ReviewResponse> getReviewsByUser(Long userId);
    List<ReviewResponse> getReviewsByStore(String storeId);
}
