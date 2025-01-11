package com.example.ticketing.util;

import com.example.ticketing.model.coupon.CouponStatus;
import com.example.ticketing.model.user.UserCoupon;
import com.example.ticketing.repository.user.UserCouponQueryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class CouponStatusScheduler {
    private final UserCouponQueryRepository userCouponQueryRepository;

    @Value("${coupon.batch.size:1000}")
    private int batchSize;

    @Scheduled(cron = "0 0 * * * *")
    public void updateExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        long lastProcessedId = 0;
        long totalProcessed = 0;

        while (true) {
            List<UserCoupon> batch = userCouponQueryRepository.findExpiredCoupons(lastProcessedId, now, batchSize);

            if (batch.isEmpty()) {
                break;
            }

            List<Long> expiredIds = batch.stream()
                    .map(UserCoupon::getId)
                    .collect(Collectors.toList());

            userCouponQueryRepository.bulkUpdateStatus(expiredIds, CouponStatus.EXPIRED);

            lastProcessedId = batch.get(batch.size() - 1).getId();
            totalProcessed += batch.size();

            log.info("Processed {} expired coupons", totalProcessed);
        }
    }
}
