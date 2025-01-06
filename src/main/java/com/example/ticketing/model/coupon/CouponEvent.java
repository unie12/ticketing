package com.example.ticketing.model.coupon;

import com.example.ticketing.model.reservation.Event;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class CouponEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String eventName;

    @Column(nullable = false)
    private LocalDateTime startTime; // 쿠폰 이벤트 시작 = 발급 시작

    @Column(nullable = false)
    private LocalDateTime endTime; // 쿠폰 이벤트 종료 = 쿠폰 발급 종료

    @Column(nullable = false)
    private LocalDateTime validityEndTime;

    @Column(nullable = false)
    private boolean isActive = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    @OneToMany(mappedBy = "couponEvent", cascade = CascadeType.ALL)
    private List<CouponTemplate> couponTemplates = new ArrayList<>();
}
