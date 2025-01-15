package com.example.ticketing.model.coupon;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CouponTemplateCreateRequest {
    private final String name;
    private final int weight;
    private final int totalQuantity;
    private final int discountAmount;
    private final DiscountType discountType;

    @Builder
    public CouponTemplateCreateRequest(String name, int weight, int totalQuantity,
                                       int discountAmount, DiscountType discountType) {
        this.name = name;
        this.weight = weight;
        this.totalQuantity = totalQuantity;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
    }

    public CouponTemplate toEntity(CouponEvent couponEvent) {
        return CouponTemplate.builder()
                .name(this.name)
                .weight(this.weight)
                .totalQuantity(this.totalQuantity)
                .discountAmount(this.discountAmount)
                .discountType(this.discountType)
                .couponEvent(couponEvent)
                .build();
    }
}