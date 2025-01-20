package com.example.ticketing.controller.user;

import com.example.ticketing.model.favorite.FavoriteDTO;
import com.example.ticketing.model.review.ReviewResponse;
import com.example.ticketing.model.user.UserActivitySummaryDTO;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.favorite.FavoriteService;
import com.example.ticketing.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-activity")
public class UserActivityController {
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/summary")
    public ResponseEntity<UserActivitySummaryDTO> getUserActivitySummary(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        List<ReviewResponse> reviews = reviewService.getReviewsByUser(userId);
        List<FavoriteDTO> myFavorites = favoriteService.getMyFavorites(userId);
        UserActivitySummaryDTO summary = UserActivitySummaryDTO.builder()
                .reviews(reviews)
                .favorites(myFavorites)
                .build();
        return ResponseEntity.ok(summary);
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponse>> getUserReviews(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        return ResponseEntity.ok(reviewService.getReviewsByUser(userId));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<FavoriteDTO>> getUserFavorites(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        return ResponseEntity.ok(favoriteService.getMyFavorites(userId));
    }

}
