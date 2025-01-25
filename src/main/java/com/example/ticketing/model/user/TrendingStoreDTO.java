package com.example.ticketing.model.user;

import com.example.ticketing.model.store.Store;
import com.example.ticketing.repository.store.StoreRepository;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Builder
public class TrendingStoreDTO {
    private String storeId;
    private String placeName;
    private String categoryGroupName;
    private Double viewScore;
    private Integer reviewCount;
    private Integer favoriteCount;

    public static List<TrendingStoreDTO> from(
            Set<ZSetOperations.TypedTuple<String>> results,
            StoreRepository storeRepository
    ) {
        if (results == null) {
            return new ArrayList<>();
        }

        List<String> storeIds = results.stream()
                .map(ZSetOperations.TypedTuple::getValue)
                .collect(Collectors.toList());

        Map<String, Store> storeMap = storeRepository.findAllById(storeIds)
                .stream()
                .collect(Collectors.toMap(Store::getId, store -> store));

        return results.stream()
                .map(tuple -> {
                    Store store = storeMap.get(tuple.getValue());
                    if (store == null) return null;

                    return TrendingStoreDTO.builder()
                            .storeId(store.getId())
                            .placeName(store.getPlaceName())
                            .categoryGroupName(store.getCategoryGroupName())
                            .viewScore(tuple.getScore())
                            .reviewCount(store.getReviewCount())
                            .favoriteCount(store.getFavoriteCount())
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
