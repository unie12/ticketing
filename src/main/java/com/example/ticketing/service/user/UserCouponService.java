package com.example.ticketing.service.user;


import com.example.ticketing.model.user.UserCoupon;
import com.example.ticketing.model.user.UserCouponDTO;

import java.util.List;

public interface UserCouponService {
    UserCouponDTO issueCoupon(Long userId, Long couponTemplateId);
    List<UserCouponDTO> getUserCoupons(Long userId);
    void useCoupon(Long userCouponId);
    UserCouponDTO getUserCoupon(Long userId, Long userCouponId);
}
