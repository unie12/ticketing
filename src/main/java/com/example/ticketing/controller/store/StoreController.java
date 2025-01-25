package com.example.ticketing.controller.store;

import com.example.ticketing.model.store.StoreDTO;
import com.example.ticketing.model.store.StoreResponseDTO;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.service.store.StoreService;
import com.example.ticketing.service.user.UserActivityLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {
    private final StoreService storeService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserActivityLogService activityLogService;

    @GetMapping("/search")
    public ResponseEntity<List<StoreResponseDTO>> searchStores(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "37.28295252865072") double latitude,
            @RequestParam(defaultValue = "127.04354383208234") double longitude,
            @RequestHeader(value = "Authorization", required = false) String token
    ) {
        Long userId = getUserIdFromToken(token);
        List<StoreResponseDTO> stores = storeService.searchNearByRestaurants(keyword, latitude, longitude, userId);

        activityLogService.logSearch(keyword, userId, stores.get(0).getId()); // id 수정
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreResponseDTO> getStoreDetail(
            @PathVariable String storeId,
            @RequestHeader(value = "Authorization", required = false) String token,
            HttpServletRequest request) {
        Long userId = getUserIdFromToken(token);
        StoreResponseDTO storeDTO = storeService.getOrFetchingStore(storeId, userId);

        String requestURI = request.getRequestURI();
        if (!requestURI.contains("/review/")) {
            activityLogService.logStoreView(storeId, userId);
        }
//        StoreDTO storeDTO = storeService.getOrFetchingStore(storeId);
        return ResponseEntity.ok(storeDTO);
    }

    private Long getUserIdFromToken(String token) {
        if (token != null && !token.isEmpty()) {
            return jwtTokenProvider.getUserIdFromToken(token.substring(7));
        }
        return null;
    }
}
