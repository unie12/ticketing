package com.example.ticketing.model.coupon;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
//@NoArgsConstructor
public class CouponEventCreateDTO {
    private final String eventName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final LocalDateTime validityEndTime;

    @Builder
    private CouponEventCreateDTO(String eventName, LocalDateTime startTime,
                                 LocalDateTime endTime, LocalDateTime validityEndTime) {
        this.eventName = eventName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.validityEndTime = validityEndTime;
    }
}
