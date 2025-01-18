package com.example.ticketing.model.store;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store {
    @Id
    private String id;

    @Column(nullable = false)
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

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreCategoryMapping> storeCategoryMappings = new ArrayList<>();

    @Builder
    public Store(String id, String placeName, String addressName, String roadAddressName, String phone, Double x, Double y, String categoryGroupCode, String categoryGroupName, String categoryName, String placeUrl) {
        this.id = id;
        this.placeName = placeName;
        this.addressName = addressName;
        this.roadAddressName = roadAddressName;
        this.phone = phone;
        this.x = x;
        this.y = y;
        this.categoryGroupCode = categoryGroupCode;
        this.categoryGroupName = categoryGroupName;
        this.categoryName = categoryName;
        this.placeUrl = placeUrl;
    }

    public void updateFromDTO(StoreDTO dto) {
        this.placeName = dto.getPlaceName();
        this.addressName = dto.getAddressName();
        this.roadAddressName = dto.getRoadAddressName();
        this.phone = dto.getPhone();
        this.x = dto.getX();
        this.y = dto.getY();
        this.categoryGroupCode = dto.getCategoryGroupCode();
        this.categoryGroupName = dto.getCategoryGroupName();
        this.placeUrl = dto.getPlaceUrl();
    }

    public void addCategory(Category category) {
        StoreCategoryMapping mapping = new StoreCategoryMapping(this, category);
    }

    public void removeCategory(Category category) {
        storeCategoryMappings.removeIf(mapping ->
                mapping.getCategory().equals(category));
    }
}
