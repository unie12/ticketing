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
            @PathVariable Long couponTemplateId
    ) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        UserCoupon userCoupon = userCouponService.issueCoupon(userId, couponTemplateId);

        return ResponseEntity.ok(UserCouponDTO.from(userCoupon));
    }

    @PostMapping("/{userCouponId}/use" )
    public ResponseEntity<String> useCoupon(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userCouponId
    ) {
        jwtTokenProvider.getUserIdFromToken(token.substring(7));
        userCouponService.useCoupon(userCouponId);

        return ResponseEntity.ok("쿠폰 사용 완료");
    }

    @GetMapping
    public ResponseEntity<List<UserCouponDTO>> getMyCoupons(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        List<UserCoupon> coupons = userCouponService.getUserCoupons(userId);

        List<UserCouponDTO> dtoList = coupons.stream()
                .map(UserCouponDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{userCouponId}")
    public ResponseEntity<UserCouponDTO> getMyCoupon(
            @RequestHeader("Authorization") String token,
            @PathVariable Long userCouponId) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        UserCoupon coupon = userCouponService.getUserCoupon(userId, userCouponId);

        UserCouponDTO dto = UserCouponDTO.from(coupon);

        return ResponseEntity.ok(dto);
    }

}
