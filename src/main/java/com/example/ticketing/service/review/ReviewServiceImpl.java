package com.example.ticketing.service.review;

import com.example.ticketing.exception.AuthException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.exception.ReviewException;
import com.example.ticketing.model.review.Review;
import com.example.ticketing.model.review.ReviewRequest;
import com.example.ticketing.model.review.ReviewResponse;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.review.ReviewRepository;
import com.example.ticketing.repository.store.StoreRepository;
import com.example.ticketing.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;

    @Override
    public ReviewResponse writeReview(ReviewRequest request, Long userId, String storeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ReviewException(ErrorCode.STORE_NOT_FOUND));

        if (reviewRepository.existsByUserAndStore(user, store)) {
            throw new ReviewException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        if (request.getContent().length() < 10) {
            throw new ReviewException(ErrorCode.INVALID_REVIEW_CONTENT);
        }

        if (request.getRating() < 1 || request.getRating() > 5) {
            throw new ReviewException(ErrorCode.INVALID_REVIEW_RATING);
        }

        Review review = Review.builder()
                .user(user)
                .store(store)
                .content(request.getContent())
                .rating(request.getRating())
                .build();

        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.from(savedReview);
    }

    @Override
    public ReviewResponse modifyReview(Long reviewId, ReviewRequest request, Long userID) {
        Review review = findReviewAndValidateUser(reviewId, userID);
        review.updateContent(request.getContent());
        review.updateRating(request.getRating());
        Review updatedReview = reviewRepository.save(review);
        return ReviewResponse.from(updatedReview);
    }

    @Override
    public ReviewResponse deleteReview(Long reviewId, Long userId) {
        Review review = findReviewAndValidateUser(reviewId, userId);
        reviewRepository.delete(review);
        return ReviewResponse.from(review);
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));
        List<Review> reviews = reviewRepository.findByUser(user);
        return reviews.stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByStore(String storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ReviewException(ErrorCode.STORE_NOT_FOUND));
        List<Review> reviews = reviewRepository.findByStore(store);
        return reviews.stream()
                .map(ReviewResponse::from)
                .collect(Collectors.toList());
    }

    private Review findReviewAndValidateUser(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));
        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewException(ErrorCode.UNAUTHORIZED_REVIEW_MODIFICATION);
        }
        return review;
    }
}
