package com.example.ticketing.service.user;

import com.example.ticketing.model.user.*;
import com.example.ticketing.repository.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealTimeAnalyticsService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long WINDOW_SIZE_HOURS = 24;
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String HOUR_FORMAT = "yyyy-MM-dd:HH";
    private final StoreRepository storeRepository;

    @KafkaListener(
            topics = "search-events",
            groupId = "analytics-group",
            containerFactory = "searchListenerContainerFactory"
    )
    public void processSearchEvents(List<SearchActivityEvent> events) {
        log.info("Received search events: {}", events);
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        for (SearchActivityEvent event : events) {
            log.info("Processing search event for query: {}", event.getQuery());
            // 전체 검색어 트렌드
            incrementTrendScore("trending:searches:" + dateStr, event.getQuery());

            // 카테고리별 검색어 트렌드
//            for (String categoryId : event.getCategoryIds()) {
//                incrementTrendScore("trending:category:" + categoryId + ":searches", event.getQuery());
//            }

            // 사용자 세그먼트별 검색어 트렌드 (나중에 연령대, 학과 등의 데이터 이용)
        }
    }

    @KafkaListener(
            topics = "store-views",
            groupId = "analytics-group",
            containerFactory = "storeViewListenerContainerFactory"
    )
    public void processStoreViews(List<StoreViewActivityEvent> events) {
        log.info("Received store view events: {}", events);
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        for (StoreViewActivityEvent event : events) {
            log.info("Processing store view event for storeId: {}", event.getStoreId());
            // 시간별 인기 가게
            incrementTrendScore("trending:stores:" + dateStr, event.getStoreId());

            incrementTrendScore("trending:stores:category:" + event.getCategoryGroupCode(), event.getStoreId());

//            // 카테고리별 인기 가게
//            for (Long categoryId : event.getCategoryIds()) {
//                incrementTrendScore("trending:category:" + categoryId + ":stores", event.getStoreId());
//            }
//
//            // 체류 시간 기반 인기도
//            if (event.getViewDuration() != null) {
//                redisTemplate.opsForZSet().incrementScore(
//                        "engagement:stores:" + dateHourStr,
//                        event.getStoreId(),
//                        event.getViewDuration()
//                );
//            }
        }
    }

    @KafkaListener(
            topics = "review-events",
            groupId = "analytics-group",
            containerFactory = "reviewListenerContainerFactory"
    )
    public void processReviewEvents(List<ReviewActivityEvent> events) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));

        for (ReviewActivityEvent event : events) {
            // 실시간 리뷰 통계
            incrementTrendScore("trending:reviews:" + dateStr, event.getStoreId());

            // 평점 통계
            if (event.getRating() != null) {
                incrementTrendScore("trending:ratings:" + dateStr, event.getStoreId());
//                redisTemplate.opsForZSet().incrementScore(
//                        "trending:ratings:" + dateStr,
//                        event.getStoreId(),
//                        event.getRating()
//                );
            }

            // 카테고리별 리뷰 트렌드
//            for (Long categoryId : event.getCategoryIds()) {
//                incrementTrendScore("trending:category:" + categoryId + ":reviews", event.getStoreId());
//            }
        }
    }

    private void incrementTrendScore(String key, String member) {
        log.info("Incrementing trend score for key: {}, member: {}", key, member);
        try {
            Double score = redisTemplate.opsForZSet().incrementScore(key, member, 1);
            if (score == null) {
                log.warn("Failed to increment score for key: {}, member: {}", key, member);
            } else {
                log.info("Successfully incremented score for key: {}, member: {}, new score: {}", key, member, score);
            }
            redisTemplate.expire(key, 24, TimeUnit.HOURS);
        } catch (Exception e) {
            log.error("Error incrementing trend score for key: {}, member: {}", key, member, e);
        }
    }


    public List<TrendingSearchDTO> getTrendingSearches(int limit) {
        String key = "trending:searches:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, limit - 1);
        return TrendingSearchDTO.from(results);
    }

    public List<TrendingStoreDTO> getTrendingStores(int limit) {
        String key = "trending:stores:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, limit - 1);
        return TrendingStoreDTO.from(results, storeRepository);
    }

    // 카테고리별 인기 가게 조회
    public List<TrendingStoreDTO> getTrendingStoresByCategory(String categoryGroupCode, int limit) {
        String key = "trending:stores:category:" + categoryGroupCode;
        Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, limit - 1);
        return TrendingStoreDTO.from(results, storeRepository);
    }

    // 리뷰 트렌드 조회
    public List<TrendingReviewDTO> getTrendingReviews(int limit) {
        String key = "trending:reviews:" + LocalDateTime.now().format(DateTimeFormatter.ofPattern(DATE_FORMAT));
        Set<ZSetOperations.TypedTuple<String>> results = redisTemplate.opsForZSet()
                .reverseRangeWithScores(key, 0, limit - 1);
        return TrendingReviewDTO.from(results, storeRepository);
    }


//    @KafkaListener(topics = "user-activities", groupId = "analytics-group")
//    public void processUserActivity(UserActivityEvent event) {
//        try {
//            switch (event.getEventType()) {
//                case STORE_VIEW:
//                    updatePopularStores(event);
//                    break;
//                case REVIEW_CREATE:
//                    updateActiveReviewers(event);
//                    break;
//                case FAVORITE_TOGGLE:
//                    updatePopularStoresByFavorites(event);
//                    break;
//            }
//        } catch (Exception e) {
//            log.error("Error processing user activity", e);
//        }
//    }

    /**
     * 인기 가게 순위
     * ZSet에 score 증가 (조회수)
     */
//    private void updatePopularStores(UserActivityEvent event) {
//        String key = "popular:stores:views:" + LocalDate.now();
//        redisTemplate.opsForZSet().incrementScore(key, event.getStoreId(), 1);
//        redisTemplate.expire(key, WINDOW_SIZE_HOURS, TimeUnit.HOURS);
//    }
//
//    private void updateActiveReviewers(UserActivityEvent event) {
//        String key = "active:reviewers:" + LocalDate.now();
//        redisTemplate.opsForZSet().incrementScore(key, event.getUserId().toString(), 1);
//        redisTemplate.expire(key, WINDOW_SIZE_HOURS, TimeUnit.HOURS);
//    }
//
//    private void updatePopularStoresByFavorites(UserActivityEvent event) {
//        String key = "popular:stores:favorites:" + LocalDate.now();
//        double score = event.getMetadata().get("action").equals("add") ? 1 : -1;
//        redisTemplate.opsForZSet().incrementScore(key, event.getStoreId(), score);
//        redisTemplate.expire(key, WINDOW_SIZE_HOURS, TimeUnit.HOURS);
//    }
//
//    /**
//     * 상위 n개 조회
//     */
//    public List<String> getTopViewedStores(int limit) {
//        String key = "popular:stores:views:" + LocalDate.now();
//        Set<String> stores = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
//
//        return stores != null ? new ArrayList<>(stores) : new ArrayList<>();
//    }
//
//    public List<String> getTopReviewers(int limit) {
//        String key = "active:reviewers:" + LocalDate.now();
//        Set<String> reviewers = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
//        return reviewers != null ? new ArrayList<>(reviewers) : new ArrayList<>();
//    }
//
//    public List<String> getTopFavoritedStores(int limit) {
//        String key = "popular:stores:favorites:" + LocalDate.now();
//        Set<String> stores = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
//        return stores != null ? new ArrayList<>(stores) : new ArrayList<>();
//    }
//
//    public Map<String, Double> getHourlyStoreViews(String storeId) {
//        Map<String, Double> hourlyViews = new HashMap<>();
//        String pattern = "popular:stores:views:*";
//        Set<String> keys = redisTemplate.keys(pattern);
//
//        if (keys != null) {
//            for (String key : keys) {
//                Double score = redisTemplate.opsForZSet().score(key, storeId);
//                if (score != null) {
//                    String hour = key.substring(key.lastIndexOf(":") + 1);
//                    hourlyViews.put(hour, score);
//                }
//            }
//        }
//        return hourlyViews;
//    }
}
