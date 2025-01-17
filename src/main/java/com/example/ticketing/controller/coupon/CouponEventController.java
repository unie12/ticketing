package com.example.ticketing.controller.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateRequest;
import com.example.ticketing.model.coupon.CouponEventResponse;
import com.example.ticketing.service.coupon.CouponEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/events/{eventId}/coupons")
public class CouponEventController {
    private final CouponEventService couponEventService;

    /**
     * 해당 couponEvent 조회
     */
    @GetMapping("/{couponEventId}")
    public ResponseEntity<CouponEventResponse> getCouponEvent(@PathVariable Long couponEventId) {
        return ResponseEntity.ok(couponEventService.getCouponEvent(couponEventId));
    }

    /**
     * 해당 event의 모든 couponEvents 조회
     */
    @GetMapping
    public ResponseEntity<List<CouponEventResponse>> getCouponEventsByEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(couponEventService.getCouponEventByEvent(eventId));
    }

    /**
     * 모든 couponEvents 조회
     */
    @GetMapping("/all")
    public ResponseEntity<List<CouponEventResponse>> getAllCouponEvents() {
        return ResponseEntity.ok(couponEventService.getCouponEvents());
    }

    /**
     * 쿠폰 이벤트 생성
     */
    @PreAuthorize("hasAnyRole('EVENT_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CouponEventResponse> createCoupon(@PathVariable Long eventId,
                                                            @RequestBody CouponEventCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponEventService.createCouponEvent(eventId, request));
    }
}
