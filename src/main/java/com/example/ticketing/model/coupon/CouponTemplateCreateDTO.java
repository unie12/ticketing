package com.example.ticketing.model.coupon;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CouponTemplateCreateDTO {
    private final String name;
    private final int weight;
    private final int totalQuantity;
    private final int discountAmount;
    private final DiscountType discountType;

    @Builder
    public CouponTemplateCreateDTO(String name, int weight, int totalQuantity, int discountAmount, DiscountType discountType) {
        this.name = name;
        this.weight = weight;
        this.totalQuantity = totalQuantity;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
    }
}
