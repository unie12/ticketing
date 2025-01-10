package com.example.ticketing.repository.coupon;

import com.example.ticketing.model.coupon.CouponTemplate;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponTemplateRepository extends JpaRepository<CouponTemplate, Long> {
    List<CouponTemplate> findByCouponEventId(Long couponEventId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CouponTemplate c SET c.remaining = c.remaining - 1 " +
            "WHERE c.id = :id AND c.remaining > 0")
    int decreaseQuantity(@Param("id") Long id);

}
