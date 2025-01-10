package com.example.ticketing.util;

import com.example.ticketing.model.coupon.CouponTemplate;
import com.example.ticketing.repository.coupon.CouponRedisRepository;
import com.example.ticketing.repository.coupon.CouponTemplateRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponDataInitializer {
    private final CouponTemplateRepository couponTemplateRepository;
    private final CouponRedisRepository couponRedisRepository;

    @PostConstruct
    public void initializeCouponData() {
        List<CouponTemplate> templates = couponTemplateRepository.findAll();
        for (CouponTemplate template : templates) {
            couponRedisRepository.initializeCouponCount(template.getId(), template.getRemaining());
        }
    }
}
