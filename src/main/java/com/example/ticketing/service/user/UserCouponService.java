package com.example.ticketing.service.user;


import com.example.ticketing.model.user.UserCoupon;

import java.util.List;

public interface UserCouponService {
    UserCoupon issueCoupon(Long userId, Long couponTemplateId);
    List<UserCoupon> getUserCoupons(Long userId);
    void useCoupon(Long userCouponId);

    UserCoupon getUserCoupon(Long userId, Long userCouponId);
}
