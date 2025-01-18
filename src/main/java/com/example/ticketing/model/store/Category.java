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
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @Column(nullable = false)
    private Integer level;

    @OneToMany(mappedBy = "category")
    private List<StoreCategoryMapping> storeCategoryMappings = new ArrayList<>();

    @Builder
    public Category(String name, Category parent, Integer level) {
        this.name = name;
        this.parent = parent;
        this.level = level;
    }

    public void addStore(Store store) {
        StoreCategoryMapping mapping = new StoreCategoryMapping(store, this);
    }
}
