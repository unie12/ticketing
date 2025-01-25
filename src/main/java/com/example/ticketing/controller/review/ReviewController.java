package com.example.ticketing.controller.review;

import com.example.ticketing.model.review.ReviewRequest;
import com.example.ticketing.model.review.ReviewResponse;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.review.ReviewService;
import com.example.ticketing.service.user.UserActivityLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review") // 후에 수정
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserActivityLogService activityLogService;

    private Long getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token.substring(7));
    }

    /**
     * POST 특정 store 리뷰 작성
     */
    @PostMapping(value = "/store/{storeId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> writeReview(
            @PathVariable String storeId,
            @ModelAttribute ReviewRequest reviewRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String token) throws IOException {
        Long userId = getUserIdFromToken(token);
        ReviewResponse reviewResponse = reviewService.writeReview(reviewRequest, images, userId, storeId);
        activityLogService.logReview(reviewResponse.getId());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewResponse);
    }

    /**
     * PUT 특정 store 리뷰 수정
     */
    @PutMapping(value = "/{reviewId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ReviewResponse> modifyReview(
            @PathVariable Long reviewId,
            @ModelAttribute ReviewRequest reviewRequest,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @RequestHeader("Authorization") String token) throws IOException {
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(reviewService.modifyReview(reviewId, reviewRequest, images, userId));
    }

    /**
     * DELETE 특정 store 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String token) {
        Long userId = getUserIdFromToken(token);;

        return ResponseEntity.ok(reviewService.deleteReview(reviewId, userId));
    }

    /**
     * GET 해당 사용자의 리뷰 목록
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByUser(@PathVariable Long userId){
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    /**
     * GET 해당 store의 리뷰 목록
     */
    @GetMapping("/store/{storeId}")
    public ResponseEntity<List<ReviewResponse>> getReviewsByStore(
            @PathVariable String storeId,
            @RequestHeader("Authorization") String token){
        Long userId = getUserIdFromToken(token);
        return ResponseEntity.ok(reviewService.getReviewsByStore(userId, storeId));
    }

    /**
     * GET 인기 리뷰 목록
     */
}
