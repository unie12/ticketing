package com.example.ticketing.model.coupon;

import com.example.ticketing.model.event.Event;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponEventCreateRequest {
    private final String eventName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final LocalDateTime validityEndTime;

    @Builder
    public CouponEventCreateRequest(String eventName, LocalDateTime startTime,
                                    LocalDateTime endTime, LocalDateTime validityEndTime) {
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.validityEndTime = validityEndTime;
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
