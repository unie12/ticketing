package com.example.ticketing.controller.store;

import com.example.ticketing.model.store.StoreDTO;
import com.example.ticketing.service.store.StoreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/store")
public class StoreController {
    private final StoreService storeService;

    @GetMapping("/search")
    public ResponseEntity<List<StoreDTO>> searchStores(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "37.28295252865072") double latitude, //default value 아주대로 설정하기
            @RequestParam(defaultValue = "127.04354383208234") double longitude
    ) {
        List<StoreDTO> stores = storeService.searchNearByRestaurants(keyword, latitude, longitude);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDTO> getStoreDetail(@PathVariable String storeId) {
        StoreDTO storeDTO = storeService.getOrFetchingStore(storeId);
        return ResponseEntity.ok(storeDTO);
    }


}
