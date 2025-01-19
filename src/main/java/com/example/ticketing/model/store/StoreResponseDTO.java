package com.example.ticketing.model.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreResponseDTO {
    private String id;
    private String placeName;
    private String addressName;
    private String roadAddressName;
    private String phone;
    private Double x;
    private Double y;
    private String categoryGroupCode;
    private String categoryGroupName;
    private String categoryName;
    private String placeUrl;
    private List<CategoryDTO> categories;
    private boolean isFavorite;

    public static StoreResponseDTO from(StoreDTO storeDTO, boolean isFavorite) {
        return StoreResponseDTO.builder()
                .id(storeDTO.getId())
                .placeName(storeDTO.getPlaceName())
                .addressName(storeDTO.getAddressName())
                .roadAddressName(storeDTO.getRoadAddressName())
                .phone(storeDTO.getPhone())
                .x(storeDTO.getX())
                .y(storeDTO.getY())
                .categoryGroupCode(storeDTO.getCategoryGroupCode())
                .categoryGroupName(storeDTO.getCategoryGroupName())
                .categoryName(storeDTO.getCategoryName())
                .placeUrl(storeDTO.getPlaceUrl())
                .categories(storeDTO.getCategories())
                .isFavorite(isFavorite)
                .build();
    }
}
