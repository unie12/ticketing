package com.example.ticketing.service.store;

import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.store.StoreDTO;
import com.example.ticketing.model.store.StoreResponseDTO;

import java.util.List;

public interface StoreService {
    List<StoreResponseDTO> searchNearByRestaurants(String keyword, double latitude, double longitude, Long userId);
    StoreDTO saveOrUpdateStore(StoreDTO storeDTO);
    StoreResponseDTO getOrFetchingStore(String storeId, Long userId);
    Store findStoreById(String storeId);

}
