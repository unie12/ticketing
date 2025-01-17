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
    private String id; // 장소 ID
    private String placeName; // 장소 이름
    private String addressName; // 지번 주소
    private String roadAddressName; // 도로명 주소
    private String phone; // 전화번호
    private Double x; // 경도 (longitude)
    private Double y; // 위도 (latitude)
    private String categoryGroupCode; // 카테고리 그룹 코드
    private String categoryGroupName; // 카테고리 그룹 이름
    private String placeUrl; // 카카오맵 URL

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
                        .placeUrl((String) document.get("place_url"))
                        .build())
                .collect(Collectors.toList());
    }

    public static StoreDTO from(Store store) {
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
                .build();
    }

    public static StoreDTO fromSingleResponse(Object response) {
        if (!(response instanceof Map)) {
            throw new IllegalArgumentException("Invalid response format");
        }
        Map<String, Object> document = (Map<String, Object>) response;
        return StoreDTO.builder()
                .id((String) document.get("id"))
                .placeName((String) document.get("place_name"))
                .addressName((String) document.get("address_name"))
                .roadAddressName((String) document.get("road_address_name"))
                .phone((String) document.get("phone"))
                .x(Double.parseDouble((String) document.get("x")))
                .y(Double.parseDouble((String) document.get("y")))
                .categoryGroupCode((String) document.get("category_group_code"))
                .categoryGroupName((String) document.get("category_group_name"))
                .placeUrl((String) document.get("place_url"))
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
