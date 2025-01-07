package com.example.ticketing.repository.coupon;

import com.example.ticketing.model.coupon.CouponTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
    List<CouponTemplate> findByCouponEventId(Long couponEventId);

}
