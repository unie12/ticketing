package com.example.ticketing.model.coupon;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponEventResponse {
    private final String eventName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final LocalDateTime validityEndTime;
    private final boolean isActive;

    @Builder
    public CouponEventResponse(String eventName, LocalDateTime startTime,
                               LocalDateTime endTime, LocalDateTime validityEndTime,
                               boolean isActive) {
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.validityEndTime = validityEndTime;
        this.isActive = isActive;
    }

    public static CouponEventResponse from(CouponEvent couponEvent) {
        return CouponEventResponse.builder()
                .eventName(couponEvent.getEventName())
                .startTime(couponEvent.getStartTime())
                .endTime(couponEvent.getEndTime())
                .validityEndTime(couponEvent.getValidityEndTime())
                .isActive(couponEvent.isActive())
                .build();
    }
}