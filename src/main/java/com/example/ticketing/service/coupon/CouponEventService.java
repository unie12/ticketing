package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateDTO;
import com.example.ticketing.model.user.UserCoupon;

import java.util.List;

public interface CouponEventService {
    CouponEvent createCouponEvent(Long eventId, CouponEventCreateDTO dto);
    CouponEvent getCouponEvent(Long couponEventId);
    List<CouponEvent> getCouponEventByEvent(Long eventId);
}
