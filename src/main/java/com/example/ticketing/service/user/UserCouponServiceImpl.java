package com.example.ticketing.service.user;

import com.example.ticketing.exception.AuthException;
import com.example.ticketing.exception.CouponException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponStatus;
import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.user.User;
import com.example.ticketing.model.user.UserCoupon;
import com.example.ticketing.model.user.UserCouponDTO;
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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserCouponServiceImpl implements UserCouponService {
    private final UserCouponRepository userCouponRepository;
    private final UserRepository userRepository;
    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponRedisRepository couponRedisRepository;

    @Override
    public UserCouponDTO issueCoupon(Long userId, Long couponTemplateId) {
        // 1. Redis 검증 먼저 수행
        boolean isAdded = couponRedisRepository.addUserToCoupon(couponTemplateId, userId);
        if (!isAdded) {
            throw new CouponException(ErrorCode.COUPON_ALREADY_OWNED);
        }

        boolean decremented = couponRedisRepository.decrementCouponCount(couponTemplateId);
        if (!decremented) {
            // Redis Set에서 사용자 제거
            couponRedisRepository.removeUserFromCoupon(couponTemplateId, userId);
            throw new CouponException(ErrorCode.COUPON_OUT_OF_STOCK);
        }

        // 2. DB 작업 수행
        try {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

            CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
                    .orElseThrow(() -> new CouponException(ErrorCode.COUPON_TEMPLATE_NOT_FOUND));

            CouponEvent event = couponTemplate.getCouponEvent();
            validateEventPeriod(event);

            // 벌크 연산으로 수량 감소
            int updatedCount = couponTemplateRepository.decreaseQuantity(couponTemplateId);
            if (updatedCount == 0) {
                throw new CouponException(ErrorCode.COUPON_UPDATE_FAILED);
            }

            UserCoupon userCoupon = UserCoupon.builder()
                    .user(user)
                    .couponTemplate(couponTemplate)
                    .build();

            return UserCouponDTO.from(userCouponRepository.save(userCoupon));
        } catch (Exception e) {
            // 실패시 Redis 롤백
            couponRedisRepository.incrementCouponCount(couponTemplateId);
            couponRedisRepository.removeUserFromCoupon(couponTemplateId, userId);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserCouponDTO> getUserCoupons(Long userId) {
        return userCouponRepository.findByUserId(userId).stream()
                .map(UserCouponDTO::from)
                .collect(Collectors.toList());
    }

    @Override
    public void useCoupon(Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_NOT_FOUND));

        validateCouponValidity(userCoupon);

        if (!userCoupon.getStatus().equals(CouponStatus.AVAILABLE)) {
            throw new CouponException(ErrorCode.COUPON_NOT_AVAILABLE);
        }

        userCoupon.setStatus(CouponStatus.USED);
        userCoupon.setUsedAt(LocalDateTime.now());
        userCouponRepository.save(userCoupon);
    }

    @Override
    public UserCouponDTO getUserCoupon(Long userId, Long userCouponId) {
        UserCoupon userCoupon = userCouponRepository.findById(userCouponId)
                .orElseThrow(() -> new CouponException(ErrorCode.COUPON_NOT_FOUND));

        return UserCouponDTO.from(userCoupon);
    }

    private void validateEventPeriod(CouponEvent event) {
        LocalDateTime now = LocalDateTime.now();
        if (!event.isActive()) {
            throw new CouponException(ErrorCode.EVENT_NOT_PERIOD);
        }
    }

    private void validateCouponValidity(UserCoupon coupon) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(coupon.getCouponTemplate().getCouponEvent().getValidityEndTime())) {
            coupon.setStatus(CouponStatus.EXPIRED);
            throw new CouponException(ErrorCode.COUPON_EVENT_EXPIRED);
        }
    }
}
