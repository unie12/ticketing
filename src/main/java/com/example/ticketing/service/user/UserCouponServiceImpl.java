package com.example.ticketing.service.user;

import com.example.ticketing.model.coupon.CouponStatus;
import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.user.User;
import com.example.ticketing.model.user.UserCoupon;
import com.example.ticketing.repository.coupon.CouponTemplateRepository;
import com.example.ticketing.repository.user.UserCouponRepository;
import com.example.ticketing.repository.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.OptimisticLockException;
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

    @Override
    public UserCoupon issueCoupon(Long userId, Long couponTemplateId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("해당 사용자를 찾을 수 없습니다: " + userId));

        CouponTemplate couponTemplate = couponTemplateRepository.findById(couponTemplateId)
                .orElseThrow(() -> new EntityNotFoundException("해당 쿠폰 템플릿을 찾을 수 없습니다: " + couponTemplateId));

        if (couponTemplate.getRemaining() <= 0) {
            throw new IllegalStateException("해당 쿠폰 템플릿이 모두 소진되었습니다.");
        }

        boolean alreadyIssued = userCouponRepository.existsByUserIdAndCouponTemplateId(userId, couponTemplateId);
        if (alreadyIssued) {
            throw new IllegalArgumentException("이미 해당 쿠폰 템플릿을 소유하고 있습니다.");
        }

        try {
            couponTemplate.decreaseRemaining();

            // 여기서 영속 상태이므로, flush 시점에 @Version 검증 수행

            UserCoupon userCoupon = UserCoupon.builder()
                    .user(user)
                    .couponTemplate(couponTemplate)
                    .build();

            userCouponRepository.save(userCoupon);
            return userCoupon;
        } catch (OptimisticLockException e) {
            throw new IllegalStateException("동시에 발급 요청이 몰려 쿠폰이 소진되었거나 충돌이 발생했습니다.");
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
