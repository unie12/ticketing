package com.example.ticketing.service.user;

import com.example.ticketing.model.user.User;
import com.example.ticketing.model.user.UserCouponDTO;
import com.example.ticketing.repository.coupon.CouponRedisRepository;
import com.example.ticketing.repository.user.UserCouponRepository;
import com.example.ticketing.repository.user.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class UserCouponServiceImplTest {

    @Autowired
    private UserCouponService userCouponService;

    @Autowired
    private CouponRedisRepository couponRedisRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private final int TOTAL_USERS = 200;
    private final int THREAD_COUNT = 200;
    private final Long COUPON_TEMPLATE_ID = 2L;  // 테스트용 쿠폰 템플릿 ID
    private final int INITIAL_COUPON_COUNT = 100;

    // 실제 DB에 생성된 User의 PK를 저장
    private final List<Long> userIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 1) Redis 키 초기화
        couponRedisRepository.initializeCouponCount(COUPON_TEMPLATE_ID, INITIAL_COUPON_COUNT);
        redisTemplate.delete("COUPON:TEMPLATE:USERS:" + COUPON_TEMPLATE_ID);

        // 2) DB 사용자/쿠폰 모두 삭제
        userCouponRepository.deleteAll();
        userRepository.deleteAll();

        // 3) 사용자 200명 생성하여 실제 PK를 userIds에 보관
        for (int i = 0; i < TOTAL_USERS; i++) {
            User user = User.builder()
                    .username("User" + (i + 1))
                    .email("user" + (i + 1) + "@example.com")
                    .password("password")
                    .build();
            userRepository.save(user);
            userIds.add(user.getId()); // DB에 의해 생성된 실제 PK
        }
    }

    @AfterEach
    void tearDown() {
        // 테스트 종료 후 DB, Redis 정리
        userCouponRepository.deleteAll();
        userRepository.deleteAll();
        redisTemplate.delete("COUPON:TEMPLATE:COUNT:" + COUPON_TEMPLATE_ID);
        redisTemplate.delete("COUPON:TEMPLATE:USERS:" + COUPON_TEMPLATE_ID);
    }

    @Test
    @Order(1)
    void testConcurrentIssueCoupons() throws InterruptedException {
        // 동시 요청 (200명)
        ExecutorService executorService = Executors.newFixedThreadPool(50);
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);

        List<String> results = Collections.synchronizedList(new ArrayList<>());

        // 200명의 userId를 각각 쓰레드에서 issueCoupon 호출
        for (int i = 0; i < THREAD_COUNT; i++) {
            final Long userId = userIds.get(i); // DB에 실제 저장된 user의 PK

            executorService.submit(() -> {
                try {
                    UserCouponDTO dto = userCouponService.issueCoupon(userId, COUPON_TEMPLATE_ID);
                    results.add(dto.toString());
                } catch (Exception ex) {
                    results.add(ex.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        // 1) DB에 저장된 쿠폰 개수
        long issuedCoupons = userCouponRepository.count();
        assertThat(issuedCoupons).isEqualTo(INITIAL_COUPON_COUNT);
        // 100개가 정확히 발급되어야 함

        // 2) Redis에 남은 쿠폰 수량
        Object remainingObj = redisTemplate.opsForValue().get("COUPON:TEMPLATE:COUNT:" + COUPON_TEMPLATE_ID);
        Long remaining;
        if (remainingObj instanceof Integer) {
            remaining = ((Integer) remainingObj).longValue();
        } else if (remainingObj instanceof Long) {
            remaining = (Long) remainingObj;
        } else if (remainingObj instanceof String) {
            remaining = Long.valueOf((String) remainingObj);
        } else {
            throw new IllegalStateException("Unexpected value type in Redis. Value=" + remainingObj);
        }
        assertThat(remaining).isEqualTo(0);  // 100개 소진 -> 남은 수량 0

        System.out.println("Results: " + results);
    }
}