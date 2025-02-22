package com.example.ticketing.model.coupon;

import com.example.ticketing.model.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponEventDTO {
    private final Long id;
    private final String eventName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final LocalDateTime validityEndTime;
    private final boolean isActive;

    @Builder
    public CouponEventDTO(Long id, String eventName, LocalDateTime startTime, LocalDateTime endTime, LocalDateTime validityEndTime, boolean isActive) {
        this.id = id;
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.validityEndTime = validityEndTime;
        this.isActive = isActive;
    }

    public static CouponEventDTO from(CouponEvent couponEvent) {
        return new CouponEventDTO(
                couponEvent.getId(), couponEvent.getEventName(),
                couponEvent.getStartTime(),
                couponEvent.getEndTime(),
                couponEvent.getValidityEndTime(),
                couponEvent.isActive()
        );
    }

    public CouponEvent toEntity(Event event) {
        return CouponEvent.builder()
                .eventName(this.eventName)
                .startTime(this.startTime)
                .endTime(this.endTime)
                .validityEndTime(this.validityEndTime)
                .event(event)
                .build();
    }

}
