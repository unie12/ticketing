package com.example.ticketing.controller.user;

import com.example.ticketing.model.user.TrendingReviewDTO;
import com.example.ticketing.model.user.TrendingSearchDTO;
import com.example.ticketing.model.user.TrendingStoreDTO;
import com.example.ticketing.service.user.RealTimeAnalyticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics/trending")
@Tag(name = "Real-time Analytics API", description = "실시간 트렌드 분석 API")
public class AnalyticsController {
    private final RealTimeAnalyticsService analyticsService;

    @GetMapping("/searches")
    @Operation(summary = "인기 검색어 조회", description = "실시간 인기 검색어 순위를 조회합니다.")
    public ResponseEntity<List<TrendingSearchDTO>> getTrendingSearches(
            @RequestParam(defaultValue = "10") @Max(100) int limit) {
        return ResponseEntity.ok(analyticsService.getTrendingSearches(limit));
    }

    @GetMapping("/stores")
    @Operation(summary = "전체 인기 가게 조회", description = "실시간 인기 가게 순위를 조회합니다.")
    public ResponseEntity<List<TrendingStoreDTO>> getTrendingStores(
            @RequestParam(defaultValue = "10") @Max(100) int limit) {
        return ResponseEntity.ok(analyticsService.getTrendingStores(limit));
    }

    @GetMapping("/stores/category/{categoryGroupCode}")
    @Operation(summary = "카테고리별 인기 가게 조회", description = "카테고리별 실시간 인기 가게 순위를 조회합니다.")
    public ResponseEntity<List<TrendingStoreDTO>> getTrendingStoresByCategory(
            @PathVariable String categoryGroupCode,
            @RequestParam(defaultValue = "10") @Max(100) int limit) {
        return ResponseEntity.ok(analyticsService.getTrendingStoresByCategory(categoryGroupCode, limit));
    }

    @GetMapping("/reviews")
    @Operation(summary = "인기 리뷰 가게 조회", description = "실시간으로 리뷰가 많이 작성되는 가게 순위를 조회합니다.")
    public ResponseEntity<List<TrendingReviewDTO>> getTrendingReviews(
            @RequestParam(defaultValue = "10") @Max(100) int limit) {
        return ResponseEntity.ok(analyticsService.getTrendingReviews(limit));
    }



//    @GetMapping("/trending/reviews")
//    public ResponseEntity<List<TrendingReviewDTo>> getTrendingReviews(@RequestParam(defaultValue = "10") int limit) {
//        return ResponseEntity.ok(analyticsService.getTrendingReviews(limit));
//    }

//    @GetMapping("/popular-stores")
//    public ResponseEntity<List<String>> getPopularStores(@RequestParam(defaultValue = "10") int limit) {
//        return ResponseEntity.ok(analyticsService.getTopViewedStores(limit));
//    }
//
//    @GetMapping("/top-reviewers")
//    public ResponseEntity<List<String>> getTopReviewers(
//            @RequestParam(defaultValue = "10") int limit) {
//        return ResponseEntity.ok(analyticsService.getTopReviewers(limit));
//    }
//
//    @GetMapping("/popular-favorites")
//    public ResponseEntity<List<String>> getPopularFavorites(
//            @RequestParam(defaultValue = "10") int limit) {
//        return ResponseEntity.ok(analyticsService.getTopFavoritedStores(limit));
//    }
//
//    @GetMapping("/store/{storeId}/hourly-views")
//    public ResponseEntity<Map<String, Double>> getHourlyStoreViews(
//            @PathVariable String storeId) {
//        return ResponseEntity.ok(analyticsService.getHourlyStoreViews(storeId));
//    }
}
