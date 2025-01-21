package com.example.ticketing.controller.user;

import com.example.ticketing.service.user.RealTimeAnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/analytics")
public class AnalyticsController {
    private final RealTimeAnalyticsService analyticsService;

    @GetMapping("/popular-stores")
    public ResponseEntity<List<String>> getPopularStores(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getTopViewedStores(limit));
    }

    @GetMapping("/top-reviewers")
    public ResponseEntity<List<String>> getTopReviewers(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getTopReviewers(limit));
    }

    @GetMapping("/popular-favorites")
    public ResponseEntity<List<String>> getPopularFavorites(
            @RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(analyticsService.getTopFavoritedStores(limit));
    }

    @GetMapping("/store/{storeId}/hourly-views")
    public ResponseEntity<Map<String, Double>> getHourlyStoreViews(
            @PathVariable String storeId) {
        return ResponseEntity.ok(analyticsService.getHourlyStoreViews(storeId));
    }
}
