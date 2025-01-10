package com.example.ticketing.service.user;

import com.example.ticketing.model.coupon.CouponStatus;
import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.user.User;
import com.example.ticketing.model.user.UserCoupon;
import com.example.ticketing.repository.coupon.CouponRedisRepository;
import com.example.ticketing.repository.coupon.CouponTemplateRepository;
import com.example.ticketing.repository.user.UserCouponRepository;
import com.example.ticketing.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService{
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponRedisRepository couponRedisRepository;

    @Override
    public UserCoupon issueCoupon(Long userId, Long couponTemplateId) {
        // 1. Redis 검증 먼저 수행
        boolean isAdded = couponRedisRepository.addUserToCoupon(couponTemplateId, userId);
        if (!isAdded) {
            throw new IllegalStateException("이미 해당 쿠폰 템플릿을 소유하고 있습니다.");
        }

        boolean decremented = couponRedisRepository.decrementCouponCount(couponTemplateId);
        if (!decremented) {
            // Redis Set에서 사용자 제거
            couponRedisRepository.removeUserFromCoupon(couponTemplateId, userId);
            throw new IllegalStateException("해당 쿠폰 템플릿이 모두 소진되었습니다.");
        }

        // 2. DB 작업 수행
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + userId));

            CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 쿠폰 템플릿을 찾을 수 없습니다: " + couponTemplateId));

            // 벌크 연산으로 수량 감소
            int updatedCount = couponTemplateRepository.decreaseQuantity(couponTemplateId);
            if (updatedCount == 0) {
                throw new IllegalStateException("쿠폰 수량 업데이트 실패");
            }

            UserCoupon userCoupon = UserCoupon.builder()
                    .user(user)
                    .couponTemplate(couponTemplate)
                    .build();

            return userCouponRepository.save(userCoupon);
        } catch (Exception e) {
            // 실패시 Redis 롤백
            couponRedisRepository.incrementCouponCount(couponTemplateId);
            couponRedisRepository.removeUserFromCoupon(couponTemplateId, userId);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCoupon> getUserCoupons(Long userId) {
        return userCouponRepository.findByUserId(userId);
    }

    @Override
    public void useCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new EntityNotFoundException("해당 쿠폰이 존재하지 않습니다: " + userCouponId ));

        if (!userCoupon.getStatus().equals(CouponStatus.AVAILABLE)) {
            throw new IllegalStateException("해당 쿠폰은 사용 불가능합니다.");
        }

        userCoupon.setStatus(CouponStatus.USED);
        userCoupon.setUsedAt(LocalDateTime.now());
        userCouponRepository.save(userCoupon);

    }
}
