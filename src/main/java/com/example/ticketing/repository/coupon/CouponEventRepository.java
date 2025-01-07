package com.example.ticketing.repository.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CouponEventRepository extends JpaRepository<CouponEvent, Long> {
    List<CouponEvent> findByEventId(Long eventId);
}
