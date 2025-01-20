package com.example.ticketing.controller.heart;

import com.example.ticketing.model.heart.HeartDTO;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.heart.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/hearts")
public class HeartController {
    private final HeartService heartService;
    private final JwtTokenProvider jwtTokenProvider;
    /**
     * POST 특정 리뷰 heartToggle
     */
    @PostMapping("/reviews/{reviewId}")
    public ResponseEntity<HeartDTO> toggleHeart(
            @PathVariable Long reviewId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));

        HeartDTO result = heartService.toggleHeart(reviewId, userId);

        return result != null ?
                ResponseEntity.ok(result) :
                ResponseEntity.noContent().build();
    }

    /**
     * GET 내가 누른 리뷰 좋아요 리스트
     */
    @GetMapping("me")
    public ResponseEntity<List<HeartDTO>> getMyHearts(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        return ResponseEntity.ok(heartService.getMyHearts(userId));
    }

}
