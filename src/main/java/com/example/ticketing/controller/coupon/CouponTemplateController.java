package com.example.ticketing.controller.coupon;

import com.example.ticketing.model.coupon.*;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.coupon.CouponTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons/{couponEventId}/templates")
public class CouponTemplateController {
    private final CouponTemplateService couponTemplateService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{templateId}")
    public ResponseEntity<CouponTemplateDTO> getCouponTemplate(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long templateId) {

        Long userId = token != null ? jwtTokenProvider.getUserIdFromToken(token.substring(7)) : null;

        return ResponseEntity.ok(couponTemplateService.getCouponTemplate(templateId, userId));
    }

    @GetMapping
    public ResponseEntity<List<CouponTemplateDTO>> getCouponTemplatesByCouponEvent(
            @RequestHeader(value = "Authorization", required = false) String token,
            @PathVariable Long couponEventId) {

        Long userId = token != null ? jwtTokenProvider.getUserIdFromToken(token.substring(7)) : null;

        return ResponseEntity.ok(couponTemplateService.getCouponTemplatesByCouponEvent(couponEventId, userId));
    }

    @PreAuthorize("hasAnyRole('EVENT_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CouponTemplateDTO> createCouponTemplate(
            @PathVariable Long couponEventId,
            @RequestBody CouponTemplateCreateRequest dto) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(couponTemplateService.createCouponTemplate(couponEventId, dto));
    }
}

