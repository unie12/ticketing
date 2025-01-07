package com.example.ticketing.controller.coupon;

import com.example.ticketing.model.coupon.*;
import com.example.ticketing.service.coupon.CouponTemplateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/couponTemplate")
public class CouponTemplateController {
    private final CouponTemplateService couponTemplateService;

    @GetMapping("/{templateId}")
    public ResponseEntity<CouponTemplateDTO> getCouponTemplate(@PathVariable Long templateId) {
        CouponTemplate couponTemplate = couponTemplateService.getCouponTemplate(templateId);
        return ResponseEntity.ok(CouponTemplateDTO.from(couponTemplate));
    }

    @GetMapping("/couponEvent/{couponEventId}")
    public ResponseEntity<List<CouponTemplateDTO>> getCouponTemplateByCouponEvent(@PathVariable Long couponEventId) {
        List<CouponTemplate> couponEvents = couponTemplateService.getCouponTemplatesByCouponEvent(couponEventId);
        List<CouponTemplateDTO> responses = couponEvents.stream()
                .map(CouponTemplateDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/{couponEventId}")
    public ResponseEntity<CouponTemplateDTO> createCouponEvent(
            @PathVariable Long couponEventId,
            @RequestBody CouponTemplateDTO dto) {
        CouponTemplate couponTemplate = couponTemplateService.createCouponTemplate(couponEventId, dto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CouponTemplateDTO.from(couponTemplate));
    }
}
