package com.example.ticketing.service.review;

import com.example.ticketing.exception.AuthException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.exception.ReviewException;
import com.example.ticketing.model.review.*;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.review.ReviewRepository;
import com.example.ticketing.repository.store.StoreRepository;
import com.example.ticketing.repository.user.UserRepository;
import com.example.ticketing.service.store.StoreService;
import com.example.ticketing.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;
    private final ImageUploadService imageUploadService;
    private final UserService userService;
    private final StoreService storeService;

    @Override
    @Transactional
    public ReviewResponse writeReview(ReviewRequest request, List<MultipartFile> images, Long userId, String storeId) throws IOException {
        User user = userService.findUserById(userId);
        Store store = storeService.findStoreById(storeId);

        if (images != null) {
            validateImages(images);
        }

        Review review = createReview(request, user, store);
        store.incrementReviewCount();

        if (images != null && !images.isEmpty()) {
            processImages(images, review);
        }

        return ReviewResponse.from(reviewRepository.save(review), user.hasLikedReview(review));
    }

    @Override
    @Transactional
    public ReviewResponse modifyReview(Long reviewId, ReviewRequest request, List<MultipartFile> images, Long userId) throws IOException {
        User user = userService.findUserById(userId);
        Review review = findReviewAndValidateUser(reviewId, userId);
        review.updateContent(request.getContent());
        review.updateRating(request.getRating());

        VisitInfo visitInfo = review.getVisitInfo();
        visitInfo.updateVisitInfo(request.getVisitDateTime(), request.getCrowdedness());

        review.getImages().clear();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                String imageUrl = imageUploadService.uploadImage(image);
                ReviewImage reviewImage = ReviewImage.builder()
                        .imageUrl(imageUrl)
                        .build();
                review.addImage(reviewImage);
            }
        }

//        Review updatedReview = reviewRepository.save(review);
        return ReviewResponse.from(review, user.hasLikedReview(review));
    }

    @Override
    @Transactional
    public ReviewResponse deleteReview(Long reviewId, Long userId) {
        User user = userService.findUserById(userId);
        Review review = findReviewAndValidateUser(reviewId, userId);
        Store store = review.getStore();
        store.decrementReviewCount();
        reviewRepository.delete(review);
        return ReviewResponse.from(review, user.hasLikedReview(review));
    }

    @Override
    public List<ReviewResponse> getReviewsByUser(Long userId) {
        User user = userService.findUserById(userId);
        List<Review> reviews = reviewRepository.findByUser(user);
        return reviews.stream()
                .map(review -> {
                    return ReviewResponse.from(review, user.hasLikedReview(review));
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getReviewsByStore(Long userId, String storeId) {
        User user = userService.findUserById(userId);
        Store store = storeService.findStoreById(storeId);
        List<Review> reviews = reviewRepository.findByStore(store);
        return reviews.stream()
                .map(review -> {
                    return ReviewResponse.from(review, user.hasLikedReview(review));
                })
                .collect(Collectors.toList());
    }

    @Override
    public Review findReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));
    }

    private Review findReviewAndValidateUser(Long reviewId, Long userId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewException(ErrorCode.REVIEW_NOT_FOUND));
        if (!review.getUser().getId().equals(userId)) {
            throw new ReviewException(ErrorCode.UNAUTHORIZED_REVIEW_MODIFICATION);
        }
        return review;
    }

    private void validateImages(List<MultipartFile> images) {
        if (images.size() > 5) {
            throw new ReviewException(ErrorCode.TOO_MANY_IMAGES);
        }

        for (MultipartFile image : images) {
            if (image.getSize() > 5_000_000) { // 5MB 제한
                throw new ReviewException(ErrorCode.IMAGE_TOO_LARGE);
            }
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new ReviewException(ErrorCode.INVALID_IMAGE_TYPE);
            }
        }
    }

    private void processImages(List<MultipartFile> images, Review review) throws IOException {
        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String imageUrl = imageUploadService.uploadImage(image);
                ReviewImage reviewImage = ReviewImage.builder()
                        .imageUrl(imageUrl)
                        .build();
                review.addImage(reviewImage);
            }
        }
    }

    private Review createReview(ReviewRequest request, User user, Store store) {
        VisitInfo visitInfo = VisitInfo.builder()
                .user(user)
                .store(store)
                .visitDateTime(request.getVisitDateTime())
                .crowdedness(request.getCrowdedness())
                .build();

        return Review.builder()
                .user(user)
                .store(store)
                .content(request.getContent())
                .rating(request.getRating())
                .visitInfo(visitInfo)
                .build();
    }
}
