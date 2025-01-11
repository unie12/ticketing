package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateRequest;

import java.util.List;

public interface CouponEventService {
    CouponEvent createCouponEvent(Long eventId, CouponEventCreateRequest request);
    CouponEvent getCouponEvent(Long couponEventId);
    List<CouponEvent> getCouponEventByEvent(Long eventId);

    List<CouponEvent> getCouponEvents();
}
