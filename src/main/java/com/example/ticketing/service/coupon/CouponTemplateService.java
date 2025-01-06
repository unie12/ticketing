package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.coupon.CouponTemplateCreateDTO;

import java.util.List;

public interface CouponTemplateService {
    CouponTemplate createCouponTemplate(Long couponEventId, CouponTemplateCreateDTO dto);
    CouponTemplate getCouponTemplate(Long templateId);
    List<CouponTemplate> getCouponTemplatesByCouponEvent(Long couponEventId);
}
