package com.example.ticketing.model.user;

import com.example.ticketing.model.coupon.CouponStatus;
import com.example.ticketing.model.coupon.CouponTemplate;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_template_id")
    private CouponTemplate couponTemplate;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @PrePersist
    public void prePersist() {
        this.issuedAt = LocalDateTime.now();
        this.status = CouponStatus.AVAILABLE;
    }

    @Builder
    private UserCoupon(User user, CouponTemplate couponTemplate, LocalDateTime usedAt) {
        this.user = user;
        this.couponTemplate = couponTemplate;
        this.usedAt = usedAt;
    }
}
