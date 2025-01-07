package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.coupon.CouponTemplateDTO;

import java.util.List;

public interface CouponTemplateService {
    CouponTemplate createCouponTemplate(Long couponEventId, CouponTemplateDTO dto);
    CouponTemplate getCouponTemplate(Long templateId);
    List<CouponTemplate> getCouponTemplatesByCouponEvent(Long couponEventId);
}
