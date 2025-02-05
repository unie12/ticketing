package com.example.ticketing.controller.user;

import com.example.ticketing.model.favorite.FavoriteDTO;
import com.example.ticketing.model.heart.HeartDTO;
import com.example.ticketing.model.recruit.RecruitmentResponseDTO;
import com.example.ticketing.model.review.ReviewResponse;
import com.example.ticketing.model.user.UserActivitySummaryDTO;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.favorite.FavoriteService;
import com.example.ticketing.service.heart.HeartService;
import com.example.ticketing.service.recruit.RecruitmentService;
import com.example.ticketing.service.review.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user-activity")
public class UserActivityController {
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;
    private final RecruitmentService recruitmentService;
    private final HeartService heartService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/summary")
    public ResponseEntity<UserActivitySummaryDTO> getUserActivitySummary(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        List<ReviewResponse> reviews = reviewService.getReviewsByUser(userId);
        List<FavoriteDTO> myFavorites = favoriteService.getMyFavorites(userId);
        List<HeartDTO> myHearts = heartService.getMyHearts(userId);
        UserActivitySummaryDTO summary = UserActivitySummaryDTO.builder()
                .reviews(reviews)
                .favorites(myFavorites)
                .hearts(myHearts)
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

    @GetMapping("/heart")
    public ResponseEntity<List<HeartDTO>> getUserHearts(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        return ResponseEntity.ok(heartService.getMyHearts(userId));
    }

    @GetMapping("/recruitment/my")
    @Operation(summary = "내 구인글 목록", description = "자신이 작성한 구인글 목록을 조회합니다.")
    public ResponseEntity<Page<RecruitmentResponseDTO>> getMyRecruitments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(recruitmentService.getMyRecruitments(userId, pageRequest));
    }

    @GetMapping("/recruitment/joined")
    @Operation(summary = "참여 구인글 목록", description = "자신이 참여한 구인글 목록을 조회합니다.")
    public ResponseEntity<Page<RecruitmentResponseDTO>> getJoinedRecruitments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String token
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(recruitmentService.getJoinedRecruitments(userId, pageRequest));
    }

}
