package com.example.ticketing.service.user;

import com.example.ticketing.model.review.Review;
import com.example.ticketing.model.user.UserActivity;
import com.example.ticketing.model.user.UserActivityEvent;
import com.example.ticketing.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserActivityLogService {
    private final KafkaTemplate<String, UserActivityEvent> kafkaTemplate;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ReviewService reviewService;

    /**
     * 사용자 활동을 이벤트 형태로 Kafka에 Produce
     * "user-activity" 토픽으로 UserActivityEvent 객체 발행 (비동기적 메시지 전송)
     */

    public void logStoreView(String storeId, Long userId) {
        UserActivityEvent event = UserActivityEvent.builder()
                .eventType(UserActivity.STORE_VIEW)
                .userId(userId)
                .storeId(storeId)
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send("user-activities", event);
        incrementStoreViewCount(storeId);
    }

    public void logReviewCreate(Long reviewId) {
        Review review = reviewService.findReviewById(reviewId);
        UserActivityEvent event = UserActivityEvent.builder()
                .eventType(UserActivity.REVIEW_CREATE)
                .userId(review.getUser().getId())
                .storeId(review.getStore().getId())
                .timestamp(LocalDateTime.now())
                .build();
        kafkaTemplate.send("user-activities", event);
    }

    public void logFavoriteToggle(String storeId, Long userId, boolean isFavorite) {
        UserActivityEvent event = UserActivityEvent.builder()
                .eventType(UserActivity.FAVORITE_TOGGLE)
                .userId(userId)
                .storeId(storeId)
                .timestamp(LocalDateTime.now())
                .metadata(Map.of("action", isFavorite ? "add" : "remove"))
                .build();
        kafkaTemplate.send("user-activities", event);
    }

    private void incrementStoreViewCount(String storeId) {
        String key = "store:views:" + storeId;
        redisTemplate.opsForValue().increment(key);
    }

}
