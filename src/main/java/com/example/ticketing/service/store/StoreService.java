package com.example.ticketing.service.store;

import com.example.ticketing.model.store.StoreDTO;

import java.util.List;

public interface StoreService {
    List<StoreDTO> searchNearByRestaurants(String keyword, double latitude, double longitude);
    StoreDTO saveOrUpdateStore(StoreDTO storeDTO);
    StoreDTO getOrFetchingStore(String storeId);

}
