package com.example.ticketing.model.store;

import com.example.ticketing.model.favorite.Favorite;
import com.example.ticketing.model.recruit.RecruitmentPost;
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

    @Column(nullable = false)
    private int reviewCount = 0;

    @Column(nullable = false)
    private int favoriteCount = 0;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreCategoryMapping> storeCategoryMappings = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL)
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecruitmentPost> recruitmentPosts = new ArrayList<>();

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

    public void incrementReviewCount() {
        this.reviewCount++;
    }

    public void decrementReviewCount() {
        this.reviewCount = Math.max(0, this.reviewCount - 1);
    }

    public void incrementFavoriteCount() {
        this.favoriteCount++;
    }

    public void decrementFavoriteCount() {
        this.favoriteCount = Math.max(0, this.favoriteCount - 1);
    }

}
