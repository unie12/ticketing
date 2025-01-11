package com.example.ticketing.repository.user;

import com.example.ticketing.model.coupon.CouponStatus;
import com.example.ticketing.model.user.QUserCoupon;
import com.example.ticketing.model.user.UserCoupon;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.ticketing.model.coupon.QCouponEvent.couponEvent;
import static com.example.ticketing.model.coupon.QCouponTemplate.couponTemplate;
import static com.example.ticketing.model.user.QUserCoupon.userCoupon;

@RequiredArgsConstructor
public class UserCouponCustomRepositoryImpl implements UserCouponCustomRepository{
    private final EntityManager em;
    private final JPAQueryFactory queryFactory;

    @Override
    public List<UserCoupon> findExpiredCoupons(Long lastId, LocalDateTime now, int limit) {
        return queryFactory
                .selectFrom(userCoupon)
                .join(userCoupon.couponTemplate, couponTemplate)
                .join(couponTemplate.couponEvent, couponEvent)
                .where(
                        userCoupon.id.gt(lastId),
                        userCoupon.status.eq(CouponStatus.AVAILABLE),
                        couponEvent.validityEndTime.before(now)
                )
                .orderBy(userCoupon.id.asc())
                .limit(limit)
                .fetch();
    }

    @Override
    public void bulkUpdateStatus(List<Long> ids, CouponStatus status) {
        queryFactory
                .update(userCoupon)
                .set(userCoupon.status, status)
                .where(userCoupon.id.in(ids))
                .execute();


        // 영속성 컨텍스트 초기화
        // 벌크 연산 후 영속성 컨텍스트 동기화 x
        em.flush();
        em.clear();
    }
}
