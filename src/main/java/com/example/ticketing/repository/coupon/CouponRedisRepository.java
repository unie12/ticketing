package com.example.ticketing.repository.coupon;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Repository;

import java.util.Collections;

@Repository
@RequiredArgsConstructor
public class CouponRedisRepository {
    private static final String COUPON_COUNT_KEY = "COUPON:TEMPLATE:COUNT:";
    private static final String COUPON_USERS_KEY = "COUPON:TEMPLATE:USERS:";

    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisTemplate stringRedisTemplate;

    /**
     * 쿠폰 수량 감소
     * redis decrement -> 원자적 처리
     */
//    public Long decrementCouponCount(Long couponTemplateId) {
//        String key = COUPON_COUNT_KEY + couponTemplateId;
//        return redisTemplate.opsForValue().decrement(key);
//    }
    public boolean decrementCouponCount(Long couponTemplateId) {
        String script = "local count = redis.call('get', KEYS[1]) " +
                "if count and tonumber(count) > 0 then " +
                "  redis.call('decr', KEYS[1]) " +
                "  return 1 " +
                "else " +
                "  return 0 " +
                "end";
        String key = "COUPON:TEMPLATE:COUNT:" + couponTemplateId;
        return stringRedisTemplate.execute(new DefaultRedisScript<>(script, Boolean.class), Collections.singletonList(key));

    }

    /**
     * 사용자 추가 -> 중복 체크
     * redis Set 자료구조 이용
     */
    public boolean addUserToCoupon(Long couponTemplateId, Long userId) {
        String key = COUPON_USERS_KEY + couponTemplateId;
        Long result = redisTemplate.opsForSet().add(key, userId.toString());
        return result != null && result == 1;
    }

    public void initializeCouponCount(Long couponTemplateId, int totalQuantity) {
        String key = COUPON_COUNT_KEY + couponTemplateId;
        redisTemplate.opsForValue().set(key, (long) totalQuantity);
    }

    public void removeUserFromCoupon(Long couponTemplateId, Long userId) {
        String key = COUPON_USERS_KEY + couponTemplateId;
        redisTemplate.opsForSet().remove(key, userId.toString());
    }

    public void incrementCouponCount(Long couponTemplateId) {
        String key = COUPON_COUNT_KEY + couponTemplateId;
        redisTemplate.opsForValue().increment(key);
    }
}
