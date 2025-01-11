//package com.example.ticketing.repository.user;
//
//import com.example.ticketing.model.coupon.CouponStatus;
//import com.example.ticketing.model.user.UserCoupon;
//import org.springframework.stereotype.Repository;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@Repository
//public interface UserCouponCustomRepository {
//    List<UserCoupon> findExpiredCoupons(Long lastId, LocalDateTime now, int limit);
//
//    void bulkUpdateStatus(List<Long> ids, CouponStatus status);
//}
