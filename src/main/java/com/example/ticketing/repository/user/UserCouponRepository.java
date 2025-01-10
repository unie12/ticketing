package com.example.ticketing.repository.user;

import com.example.ticketing.model.user.UserCoupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserCouponRepository extends JpaRepository<UserCoupon, Long> {
    boolean existsByUserIdAndCouponTemplateId(Long userId, Long couponTemplateId);

    List<UserCoupon> findByUserId(Long userId);
}
