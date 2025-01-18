package com.example.ticketing.controller.review;

import com.example.ticketing.model.review.ReviewRequest;
import com.example.ticketing.model.review.ReviewResponse;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/review") // 후에 수정
public class ReviewController {
    private final ReviewService reviewService;
    private final JwtTokenProvider jwtTokenProvider;

    private Long getUserIdFromToken(String token) {
        return jwtTokenProvider.getUserIdFromToken(token.substring(7));
    }

    /**
     * POST 특정 store 리뷰 작성
     */
    @PostMapping("/store/{storeId}")
    public ResponseEntity<ReviewResponse> writeReview(
            @PathVariable String storeId,
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));;

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reviewService.writeReview(request, userId, storeId));
    }

    /**
     * PUT 특정 store 리뷰 수정
     */
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> modifyReview(
            @PathVariable Long reviewId,
            @RequestBody ReviewRequest request,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));;

        return ResponseEntity.ok(reviewService.modifyReview(reviewId, request, userId));
    }

    /**
     * DELETE 특정 store 리뷰 삭제
     */
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> deleteReview(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));;

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
    public ResponseEntity<List<ReviewResponse>> getReviewsByStore(@PathVariable String storeId){
        return ResponseEntity.ok(reviewService.getReviewsByStore(storeId));
    }

    /**
     * GET 인기 리뷰 목록
     */
}
