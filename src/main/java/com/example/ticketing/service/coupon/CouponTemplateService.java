package com.example.ticketing.service.coupon;

import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.model.coupon.CouponTemplateCreateRequest;
import com.example.ticketing.model.coupon.CouponTemplateDTO;

import java.util.List;

public interface CouponTemplateService {
    CouponTemplateDTO createCouponTemplate(Long couponEventId, CouponTemplateCreateRequest dto);
    CouponTemplateDTO getCouponTemplate(Long templateId, Long userId);
    List<CouponTemplateDTO> getCouponTemplatesByCouponEvent(Long couponEventId, Long userId);
}
