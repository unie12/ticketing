package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateRequest;
import com.example.ticketing.model.coupon.CouponEventResponse;

import java.util.List;

public interface CouponEventService {
    CouponEventResponse createCouponEvent(Long eventId, CouponEventCreateRequest request);
    CouponEventResponse getCouponEvent(Long couponEventId);
    List<CouponEventResponse> getCouponEventByEvent(Long eventId);
    List<CouponEventResponse> getCouponEvents();
}
