package com.example.ticketing.model.store;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDTO {
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

    public static List<StoreDTO> from(Object response) {
        if (!(response instanceof Map)) {
            throw new IllegalArgumentException("Invalid response format");
        }

        Map<String, Object> responseMap = (Map<String, Object>) response;
        List<Map<String, Object>> documents = (List<Map<String, Object>>) responseMap.get("documents");

        return documents.stream()
                .map(document -> StoreDTO.builder()
                        .id((String) document.get("id"))
                        .placeName((String) document.get("place_name"))
                        .addressName((String) document.get("address_name"))
                        .roadAddressName((String) document.get("road_address_name"))
                        .phone((String) document.get("phone"))
                        .x(Double.parseDouble((String) document.get("x")))
                        .y(Double.parseDouble((String) document.get("y")))
                        .categoryGroupCode((String) document.get("category_group_code"))
                        .categoryGroupName((String) document.get("category_group_name"))
                        .categoryName((String) document.get("category_name"))
                        .placeUrl((String) document.get("place_url"))
                        .build())
                .collect(Collectors.toList());
    }

    public static StoreDTO from(Store store) {
        List<CategoryDTO> categoryDTOs = store.getStoreCategoryMappings().stream()
                .map(mapping -> CategoryDTO.from(mapping.getCategory()))
                .collect(Collectors.toList());

        return StoreDTO.builder()
                .id(store.getId())
                .placeName(store.getPlaceName())
                .addressName(store.getAddressName())
                .roadAddressName(store.getRoadAddressName())
                .phone(store.getPhone())
                .x(store.getX())
                .y(store.getY())
                .categoryGroupCode(store.getCategoryGroupCode())
                .categoryGroupName(store.getCategoryGroupName())
                .placeUrl(store.getPlaceUrl())
                .categories(categoryDTOs)
                .build();
    }

    public Store toEntity() {
        return Store.builder()
                .id(id)
                .placeName(placeName)
                .addressName(addressName)
                .roadAddressName(roadAddressName)
                .phone(phone)
                .x(x)
                .y(y)
                .categoryGroupCode(categoryGroupCode)
                .categoryGroupName(categoryGroupName)
                .placeUrl(placeUrl)
                .build();
    }
}