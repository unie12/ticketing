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
        CouponEvent couponEvent = couponEventService.getCouponEvent(couponEventId);
        return ResponseEntity.ok(CouponEventResponse.from(couponEvent));
    }

    /**
     * 해당 event의 모든 couponEvent 조회
     */
    @GetMapping
    public ResponseEntity<List<CouponEventResponse>> getCouponEventByEvent(@PathVariable Long eventId) {
        List<CouponEvent> couponEvents = couponEventService.getCouponEventByEvent(eventId);
        List<CouponEventResponse> responses = couponEvents.stream()
                .map(CouponEventResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * 모든 couponEvent 조회
     */
    @GetMapping
    public ResponseEntity<List<CouponEventResponse>> getCouponEvents() {
        List<CouponEvent> couponEvents = couponEventService.getCouponEvents();
        List<CouponEventResponse> responses = couponEvents.stream()
                .map(CouponEventResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('EVENT_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CouponEventResponse> createCouponEvent(
            @PathVariable Long eventId,
            @RequestBody CouponEventCreateRequest request) {
        CouponEvent couponEvent = couponEventService.createCouponEvent(eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CouponEventResponse.from(couponEvent));
    }
}