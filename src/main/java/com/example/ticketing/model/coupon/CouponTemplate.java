package com.example.ticketing.model.coupon;

import com.example.ticketing.model.user.UserCoupon;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CouponTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
