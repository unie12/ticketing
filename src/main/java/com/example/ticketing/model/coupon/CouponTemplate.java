package com.example.ticketing.model.coupon;

import com.example.ticketing.model.user.UserCoupon;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @Version
//    private Long version; // optimistic lock verison

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private int totalQuantity;

    @Column(nullable = false)
    private int remaining;

    @Column(nullable = false)
    private int discountAmount; // 할인 금액

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DiscountType discountType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_event_id")
    private CouponEvent couponEvent;

    @OneToMany(mappedBy = "couponTemplate", cascade = CascadeType.ALL)
    private List<UserCoupon> userCoupons = new ArrayList<>();

    @Builder
    private CouponTemplate(String name, int weight, int totalQuantity,
                           int discountAmount, DiscountType discountType, CouponEvent couponEvent) {
        this.name = name;
        this.weight = weight;
        this.totalQuantity = totalQuantity;
        this.remaining = totalQuantity;
        this.discountAmount = discountAmount;
        this.discountType = discountType;
        this.couponEvent = couponEvent;
        this.userCoupons = new ArrayList<>();
    }

    public void decreaseRemaining() {
        if (this.remaining <= 0) {
            throw new IllegalStateException("해당 쿠폰 템플릿이 모두 소진되었습니다.");
        }
        this.remaining--;
    }
}
