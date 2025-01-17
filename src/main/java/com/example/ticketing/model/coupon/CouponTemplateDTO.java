package com.example.ticketing.model.coupon;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CouponTemplateDTO {
    private final Long id;
    private final String name;
    private final int weight;
    private final int totalQuantity;
    private final int remaining;
    private final int discountAmount;
    private final DiscountType discountType;
    private final LocalDateTime validityEndTime;
    private final boolean isOwned;

    @Builder
    public CouponTemplateDTO(Long id, String name, int weight, int totalQuantity,
                             int remaining, int discountAmount, DiscountType discountType,
                             LocalDateTime eventEndTime, LocalDateTime validityEndTime,
                             boolean isOwned) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.totalQuantity = totalQuantity;
        this.remaining = remaining;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.validityEndTime = validityEndTime;
        this.isOwned = isOwned;
    }

    public static CouponTemplateDTO from(CouponTemplate entity, boolean isOwned) {
        return CouponTemplateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .weight(entity.getWeight())
                .totalQuantity(entity.getTotalQuantity())
                .remaining(entity.getRemaining())
                .discountAmount(entity.getDiscountAmount())
                .discountType(entity.getDiscountType())
                .eventEndTime(entity.getCouponEvent().getEndTime())
                .validityEndTime(entity.getCouponEvent().getValidityEndTime())
                .isOwned(isOwned)
                .build();
    }
}
