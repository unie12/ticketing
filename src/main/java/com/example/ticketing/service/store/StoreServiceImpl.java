package com.example.ticketing.service.store;

import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.exception.ReviewException;
import com.example.ticketing.model.store.*;
import com.example.ticketing.repository.favorite.FavoriteRepository;
import com.example.ticketing.repository.store.CategoryRepository;
import com.example.ticketing.repository.store.StoreCategoryMappingRepository;
import com.example.ticketing.repository.store.StoreRepository;
import com.example.ticketing.service.KakaoMapService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final KakaoMapService kakaoMapService;
    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final StoreCategoryMappingRepository storeCategoryMappingRepository;
    private final CacheManager cacheManager;
    private final FavoriteRepository favoriteRepository;

    @Override
    @Transactional
    public List<StoreResponseDTO> searchNearByRestaurants(String keyword, double latitude, double longitude, Long userId) {
        Object response = kakaoMapService.searchPlaces(keyword, latitude, longitude);
        List<StoreDTO> stores = StoreDTO.from(response);

        Set<String> favoriteStoreIds;
        if (userId != null) {
            favoriteStoreIds = favoriteRepository.findStoreIdsByUserId(userId);
        } else {
            favoriteStoreIds = new HashSet<>();
        }

        List<StoreResponseDTO> result = stores.stream()
                .map(store -> StoreResponseDTO.from(store, favoriteStoreIds.contains(store.getId())))
                .collect(Collectors.toList());

        // 검색 결과를 캐시에 저장
        stores.forEach(store -> {
            cacheManager.getCache("stores").put(store.getId(), store);
        });

        return result;
    }

    @Override
    @Transactional
    public StoreDTO saveOrUpdateStore(StoreDTO storeDTO) {
        Store store = storeRepository.findById(storeDTO.getId())
                .map(existingStore -> {
                    existingStore.updateFromDTO(storeDTO);
                    // 기존 카테고리 매핑 삭제
                    storeCategoryMappingRepository.deleteByStoreId(existingStore.getId());
                    // 새로운 카테고리 처리
                    if (storeDTO.getCategoryName() != null) {
                        processCategoryString(existingStore, storeDTO.getCategoryName());
                    }
                    return existingStore;
                })
                .orElseGet(() -> {
                    Store newStore = storeDTO.toEntity();
                    // 새로운 카테고리 처리
                    if (storeDTO.getCategoryName() != null) {
                        processCategoryString(newStore, storeDTO.getCategoryName());
                    }
                    return storeRepository.save(newStore);
                });

        return StoreDTO.from(store);
    }

    @Override
    @Transactional
    public StoreResponseDTO  getOrFetchingStore(String storeId, Long userId) {

        StoreDTO storeDTO = storeRepository.findById(storeId)
                .map(StoreDTO::from)
                .orElseGet(() -> {
                    Cache.ValueWrapper cached = cacheManager.getCache("stores").get(storeId);
                    if (cached != null) {
                        return (StoreDTO) cached.get();
                    }
                    throw new RuntimeException("Store not found: " + storeId);
                });

        boolean isFavorite = false;
        if (userId != null) {
            isFavorite = favoriteRepository.existsByUserIdAndStoreId(userId, storeId);
        }

        return StoreResponseDTO.from(storeDTO, isFavorite);

//        return storeRepository.findById(storeId)
//                .map(StoreDTO::from)
//                .orElseGet(() -> {
//                    Cache.ValueWrapper cached = cacheManager.getCache("stores")
//                            .get(storeId);
//
//                    if (cached != null) {
//                        StoreDTO cachedStore = (StoreDTO) cached.get();
//                        Store store = cachedStore.toEntity();
//
//                        // 카테고리 처리
//                        if (cachedStore.getCategoryName() != null) {
//                            processCategoryString(store, cachedStore.getCategoryName());
//                        }
//
//                        Store savedStore = storeRepository.save(store);
//                        return StoreDTO.from(savedStore);
//                    }
//
//                    throw new RuntimeException("Store not found: " + storeId);
//                });
    }

    private void processCategoryString(Store store, String categoryString) {
        if (categoryString == null || categoryString.trim().isEmpty()) {
            return;
        }

        String[] categoryLevels = categoryString.split(" > ");
        Category parent = null;

        for (int i = 0; i < categoryLevels.length; i++) {
            final String trimmedCategoryName = categoryLevels[i].trim();
            final int currentLevel = i + 1;

            Category finalParent = parent;
            Category category = categoryRepository.findByNameAndLevel(trimmedCategoryName, currentLevel)
                    .orElseGet(() -> categoryRepository.save(
                            Category.builder()
                                    .name(trimmedCategoryName)
                                    .parent(finalParent)
                                    .level(currentLevel)
                                    .build()
                    ));

            store.addCategory(category);  // 연관관계 편의 메서드 사용

            parent = category;
        }

        storeRepository.save(store);  // 변경된 엔티티 저장
    }



    // 카테고리 조회 메서드 추가
    @Transactional(readOnly = true)
    public List<Category> getStoreCategories(String storeId) {
        return storeCategoryMappingRepository.findByStoreId(storeId).stream()
                .map(StoreCategoryMapping::getCategory)
                .collect(Collectors.toList());
    }

    @Override
    public Store findStoreById(String storeId) {
        return storeRepository.findById(storeId)
                .orElseThrow(() -> new ReviewException(ErrorCode.STORE_NOT_FOUND));
    }
}
