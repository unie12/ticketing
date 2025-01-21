package com.example.ticketing.controller.favorite;

import com.example.ticketing.model.favorite.FavoriteDTO;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.favorite.FavoriteService;
import com.example.ticketing.service.user.UserActivityLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorite") // 후에 수정
public class FavoriteController {
    private final FavoriteService favoriteService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserActivityLogService activityLogService;

    /**
     * POST 특정 store 찜 toggle
     */
    @PostMapping("/{storeId}")
    public ResponseEntity<FavoriteDTO> toggleFavorite(
            @PathVariable String storeId,
            @RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        FavoriteDTO result = favoriteService.toggleFavorite(storeId, userId);
        activityLogService.logFavoriteToggle(storeId, userId, result.isFavorite());

        return ResponseEntity.ok(favoriteService.toggleFavorite(storeId, userId));
    }

    /**
     * GET 해당 사용자의 찜 목록
     */
    @GetMapping
    public ResponseEntity<List<FavoriteDTO>> getMyFavorites(@RequestHeader("Authorization") String token) {
        Long userId = jwtTokenProvider.getUserIdFromToken(token.substring(7));
        return ResponseEntity.ok(favoriteService.getMyFavorites(userId));
    }

}
