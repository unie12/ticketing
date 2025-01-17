package com.example.ticketing.service.store;

import com.example.ticketing.model.store.Store;
import com.example.ticketing.model.store.StoreDTO;
import com.example.ticketing.repository.store.StoreRepository;
import com.example.ticketing.service.KakaoMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {
    private final KakaoMapService kakaoMapService;
    private final StoreRepository storeRepository;

    @Override
    public List<StoreDTO> searchNearByRestaurants(String keyword, double latitude, double longitude) {
        Object response = kakaoMapService.searchPlaces(keyword, latitude, longitude);
        return StoreDTO.from(response);
    }

    @Override
    public StoreDTO saveOrUpdateStore(StoreDTO storeDTO) {
        Store store = storeRepository.findById(storeDTO.getId())
                .map(existingStore -> {
                    existingStore.updateFromDTO(storeDTO);
                    return existingStore;
                })
                .orElseGet(() -> storeRepository.save(storeDTO.toEntity()));
        return StoreDTO.from(store);
    }

    @Override
    public StoreDTO getOrFetchingStore(String storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseGet(() -> {
                    Object placeDetail = kakaoMapService.getPlaceDetail(storeId);
                    StoreDTO storeDTO = StoreDTO.fromSingleResponse(placeDetail);
                    return storeRepository.save(storeDTO.toEntity());
                });
        return StoreDTO.from(store);
    }
}
