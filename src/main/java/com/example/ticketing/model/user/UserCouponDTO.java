package com.example.ticketing.model.user;

import com.example.ticketing.model.coupon.CouponStatus;
import com.example.ticketing.model.coupon.CouponTemplateDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserCouponDTO {
    private final Long id;
    private final LocalDateTime issuedAt;
    private final LocalDateTime usedAt;
    private final CouponStatus status;

    private final CouponTemplateDTO couponTemplate;

    public static UserCouponDTO from(UserCoupon userCoupon) {
        return UserCouponDTO.builder()
                .id(userCoupon.getId())
                .issuedAt(userCoupon.getIssuedAt())
                .usedAt(userCoupon.getUsedAt())
                .status(userCoupon.getStatus())
                .couponTemplate(CouponTemplateDTO.from(userCoupon.getCouponTemplate()))
                .build();
    }
}
