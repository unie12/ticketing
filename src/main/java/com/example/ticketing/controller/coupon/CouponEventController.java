package com.example.ticketing.controller.coupon;

import com.example.ticketing.model.coupon.CouponEvent;
import com.example.ticketing.model.coupon.CouponEventCreateRequest;
import com.example.ticketing.model.coupon.CouponEventResponse;
import com.example.ticketing.service.coupon.CouponEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/couponEvent")
public class CouponEventController {
    private final CouponEventService couponEventService;

    @GetMapping("/{couponEventId}")
    public ResponseEntity<CouponEventResponse> getCouponEvent(@PathVariable Long couponEventId) {
        CouponEvent couponEvent = couponEventService.getCouponEvent(couponEventId);
        return ResponseEntity.ok(CouponEventResponse.from(couponEvent));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CouponEventResponse>> getCouponEventByEvent(@PathVariable Long eventId) {
        List<CouponEvent> couponEvents = couponEventService.getCouponEventByEvent(eventId);
        List<CouponEventResponse> responses = couponEvents.stream()
                .map(CouponEventResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{eventId}")
    public ResponseEntity<CouponEventResponse> createCouponEvent(
            @PathVariable Long eventId,
            @RequestBody CouponEventCreateRequest request) {
        CouponEvent couponEvent = couponEventService.createCouponEvent(eventId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CouponEventResponse.from(couponEvent));
    }
}