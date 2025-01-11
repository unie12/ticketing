package com.example.ticketing.model.event;

import com.example.ticketing.model.coupon.CouponEvent;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@ToString(exclude = "reservations")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int totalSeats;

    @Column(nullable = false)
    private int remainingSeats;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime startTime; // 이벤트 시작

    @Column(nullable = false)
    private LocalDateTime endTime; // 이벤트 종료

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reservation> reservations = new ArrayList<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL)
    private List<CouponEvent> couponEvents = new ArrayList<>();

    @Builder
    private Event(String name, int totalSeats, LocalDateTime startTime, LocalDateTime endTime) {
        this.name = name;
        this.totalSeats = totalSeats;
        this.remainingSeats = totalSeats;
        this.createdAt = LocalDateTime.now();
        this.startTime = startTime;
        this.endTime = endTime;
        this.reservations = new ArrayList<>();
        this.couponEvents = new ArrayList<>();
    }
}
