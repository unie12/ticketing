package com.example.ticketing.model.store;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "store_category_mapping")
public class StoreCategoryMapping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Builder
    public StoreCategoryMapping(Store store, Category category) {
        setStore(store);
        setCategory(category);
    }

    // 연관관계 편의 메서드
    private void setStore(Store store) {
        this.store = store;
        if (store != null) {
            store.getStoreCategoryMappings().add(this);
        }
    }

    private void setCategory(Category category) {
        this.category = category;
        if (category != null) {
            category.getStoreCategoryMappings().add(this);
        }
    }
}