package com.example.ticketing.controller.user;

import com.example.ticketing.model.user.UserCoupon;
import com.example.ticketing.model.user.UserCouponDTO;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.user.UserCouponService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/userCoupons")
public class UserCouponController {
    private final UserCouponService userCouponService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/{couponTemplateId}/issue")
    public ResponseEntity<UserCouponDTO> issueCoupon(
            @RequestHeader("Authorization") String token,
            @PathVariable Long couponTemplateId) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));

        return ResponseEntity.ok(userCouponService.issueCoupon(userId, couponTemplateId));
    }

    @PostMapping("/{userCouponId}/use")
    public ResponseEntity<String> useCoupon(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userCouponId) {

        jwtTokenProvider.getUserIdFromToken(token.substring(7));

        userCouponService.useCoupon(userCouponId);

        return ResponseEntity.ok("쿠폰 사용 완료");
    }

    @GetMapping
    public ResponseEntity<List<UserCouponDTO>> getMyCoupons(@RequestHeader("Authorization") String token) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));

        return ResponseEntity.ok(userCouponService.getUserCoupons(userId));
    }

    @GetMapping("/{userCouponId}")
    public ResponseEntity<UserCouponDTO> getMyCoupons(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userCouponId) {

        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));

        return ResponseEntity.ok(userCouponService.getUserCoupon(userId, userCouponId));
    }
}
