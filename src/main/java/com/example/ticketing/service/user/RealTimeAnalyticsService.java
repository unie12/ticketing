package com.example.ticketing.service.user;

import com.example.ticketing.model.user.UserActivityEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class RealTimeAnalyticsService {
    private final RedisTemplate<String, String> redisTemplate;
    private static final long WINDOW_SIZE_HOURS = 24;

    @KafkaListener(topics = "user-activities", groupId = "analytics-group")
    public void processUserActivity(UserActivityEvent event) {
        try {
            switch (event.getEventType()) {
                case STORE_VIEW:
                    updatePopularStores(event);
                    break;
                case REVIEW_CREATE:
                    updateActiveReviewers(event);
                    break;
                case FAVORITE_TOGGLE:
                    updatePopularStoresByFavorites(event);
                    break;
            }
        } catch (Exception e) {
            log.error("Error processing user activity", e);
        }

    }

    private void updatePopularStores(UserActivityEvent event) {
        String key = "popular:stores:views:" + LocalDate.now();
        redisTemplate.opsForZSet().incrementScore(key, event.getStoreId(), 1);
        redisTemplate.expire(key, WINDOW_SIZE_HOURS, TimeUnit.HOURS);
    }

    private void updateActiveReviewers(UserActivityEvent event) {
        String key = "active:reviewers:" + LocalDate.now();
        redisTemplate.opsForZSet().incrementScore(key, event.getUserId().toString(), 1);
        redisTemplate.expire(key, WINDOW_SIZE_HOURS, TimeUnit.HOURS);
    }

    private void updatePopularStoresByFavorites(UserActivityEvent event) {
        String key = "popular:stores:favorites:" + LocalDate.now();
        double score = event.getMetadata().get("action").equals("add") ? 1 : -1;
        redisTemplate.opsForZSet().incrementScore(key, event.getStoreId(), score);
        redisTemplate.expire(key, WINDOW_SIZE_HOURS, TimeUnit.HOURS);
    }

    public List<String> getTopViewedStores(int limit) {
        String key = "popular:stores:views:" + LocalDate.now();
        Set<String> stores = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);

        return stores != null ? new ArrayList<>(stores) : new ArrayList<>();
    }

    public List<String> getTopReviewers(int limit) {
        String key = "active:reviewers:" + LocalDate.now();
        Set<String> reviewers = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        return reviewers != null ? new ArrayList<>(reviewers) : new ArrayList<>();
    }

    public List<String> getTopFavoritedStores(int limit) {
        String key = "popular:stores:favorites:" + LocalDate.now();
        Set<String> stores = redisTemplate.opsForZSet().reverseRange(key, 0, limit - 1);
        return stores != null ? new ArrayList<>(stores) : new ArrayList<>();
    }

    public Map<String, Double> getHourlyStoreViews(String storeId) {
        Map<String, Double> hourlyViews = new HashMap<>();
        String pattern = "popular:stores:views:*";
        Set<String> keys = redisTemplate.keys(pattern);

        if (keys != null) {
            for (String key : keys) {
                Double score = redisTemplate.opsForZSet().score(key, storeId);
                if (score != null) {
                    String hour = key.substring(key.lastIndexOf(":") + 1);
                    hourlyViews.put(hour, score);
                }
            }
        }
        return hourlyViews;
    }
}
