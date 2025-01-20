package com.example.ticketing.service.review;

import com.example.ticketing.model.review.ReviewRequest;
import com.example.ticketing.model.review.ReviewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ReviewService {
    ReviewResponse writeReview(ReviewRequest request, List<MultipartFile> images, Long userId, String storeId) throws IOException;
    ReviewResponse modifyReview(Long reviewId, ReviewRequest request, List<MultipartFile> images, Long userId) throws IOException;
    ReviewResponse deleteReview(Long reviewId, Long userId);
    List<ReviewResponse> getReviewsByUser(Long userId);
    List<ReviewResponse> getReviewsByStore(String storeId);
}
