package com.example.ticketing.controller.coupon;

import com.example.ticketing.model.coupon.*;
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

    @GetMapping("/{templateId}")
    public ResponseEntity<CouponTemplateDTO> getCouponTemplate(@PathVariable Long templateId) {
        CouponTemplate couponTemplate = couponTemplateService.getCouponTemplate(templateId);
        return ResponseEntity.ok(CouponTemplateDTO.from(couponTemplate));
    }

    @GetMapping
    public ResponseEntity<List<CouponTemplateDTO>> getCouponTemplateByCouponEvent(@PathVariable Long couponEventId) {
        List<CouponTemplate> couponEvents = couponTemplateService.getCouponTemplatesByCouponEvent(couponEventId);
        List<CouponTemplateDTO> responses = couponEvents.stream()
                .map(CouponTemplateDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PreAuthorize("hasAnyRole('EVENT_MANAGER', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CouponTemplateDTO> createCouponTemplate(
            @PathVariable Long couponEventId,
            @RequestBody CouponTemplateDTO dto) {
        CouponTemplate couponTemplate = couponTemplateService.createCouponTemplate(couponEventId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CouponTemplateDTO.from(couponTemplate));
    }
}
