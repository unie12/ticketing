package com.example.ticketing.service.user;

import com.example.ticketing.model.review.Review;
import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.store.StoreResponseDTO;
import com.example.ticketing.model.user.*;
import com.example.ticketing.repository.store.StoreRepository;
import com.example.ticketing.service.review.ReviewService;
import com.example.ticketing.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserActivityLogService {
//    private final KafkaTemplate<String, UserActivityEvent> kafkaTemplate;

    private final KafkaTemplate<String, SearchActivityEvent> searchKafkaTemplate;
    private final KafkaTemplate<String, StoreViewActivityEvent> storeViewKafkaTemplate;
    private final KafkaTemplate<String, ReviewActivityEvent> reviewKafkaTemplate;
    private final StoreRepository storeRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ReviewService reviewService;
    private final StoreService storeService;

    /**
     * 사용자 활동을 이벤트 형태로 Kafka에 Produce
     * "user-activity" 토픽으로 UserActivityEvent 객체 발행 (비동기적 메시지 전송)
     */

    public void logSearch(String query, Long userId, String storeId) {
//        Store store = storeRepository.findById(storeId).orElseThrow();
        StoreResponseDTO dto = storeService.getOrFetchingStore(storeId, null);

        SearchActivityEvent event = SearchActivityEvent.builder()
                .eventType(UserActivity.SEARCH)
                .userId(userId)
                .storeId(dto.getId())
                .timestamp(LocalDateTime.now())
                .query(query)
                .categoryGroupCode(dto.getCategoryGroupCode())
                .build();

        searchKafkaTemplate.send("search-events", event)
                .thenAccept(result -> log.debug("Search event sent successfully: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    log.error("Failed to send search event", ex);
                    return null;
                });
    }

    public void logStoreView(String storeId, Long userId) {
        Store store = storeRepository.findById(storeId).orElseThrow();

        StoreViewActivityEvent event = StoreViewActivityEvent.builder()
                .eventType(UserActivity.STORE_VIEW)
                .userId(userId)
                .storeId(storeId)
                .timestamp(LocalDateTime.now())
                .categoryGroupCode(store.getCategoryGroupCode())
                .categoryGroupName(store.getCategoryGroupName())
                .viewDuration(0.0)  // 추후 구현
                .build();

        storeViewKafkaTemplate.send("store-views", event)
                .thenAccept(result -> log.debug("Store view event sent successfully: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    log.error("Failed to send search event", ex);
                    return null;
                });
    }

    public void logReview(Long reviewId) {
        Review review = reviewService.findReviewById(reviewId);
        Store store = review.getStore();

        ReviewActivityEvent event = ReviewActivityEvent.builder()
                .eventType(UserActivity.REVIEW_CREATE)
                .userId(review.getUser().getId())
                .storeId(store.getId())
                .timestamp(LocalDateTime.now())
                .reviewId(reviewId)
                .rating(review.getRating())
                .categoryGroupCode(store.getCategoryGroupCode())
                .build();

        reviewKafkaTemplate.send("review-events", event)
                .thenAccept(result -> log.debug("Review event sent successfully: topic={}, partition={}, offset={}",
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()))
                .exceptionally(ex -> {
                    log.error("Failed to send search event", ex);
                    return null;
                });
    }

//    public void logFavoriteToggle(String storeId, Long userId, boolean isFavorite) {
//        UserActivityEvent event = UserActivityEvent.builder()
//                .eventType(UserActivity.FAVORITE_TOGGLE)
//                .userId(userId)
//                .storeId(storeId)
//                .timestamp(LocalDateTime.now())
//                .metadata(Map.of("action", isFavorite ? "add" : "remove"))
//                .build();
//        kafkaTemplate.send("user-activities", event);
//    }

    private void incrementStoreViewCount(String storeId) {
        String key = "store:views:" + storeId;
        redisTemplate.opsForValue().increment(key);
    }

}
