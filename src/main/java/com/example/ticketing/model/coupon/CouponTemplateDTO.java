package com.example.ticketing.model.coupon;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CouponTemplateDTO {
    private final Long id;
    private final String name;
    private final int weight;
    private final int totalQuantity;
    private final int discountAmount;
    private final DiscountType discountType;

    @Builder
    public CouponTemplateDTO(Long id, String name, int weight, int totalQuantity, int discountAmount, DiscountType discountType) {
        this.id = id;
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

    public static CouponTemplateDTO from(CouponTemplate entity) {
        return CouponTemplateDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .weight(entity.getWeight())
                .totalQuantity(entity.getTotalQuantity())
                .discountAmount(entity.getDiscountAmount())
                .discountType(entity.getDiscountType())
                .build();
    }
}
