package com.example.ticketing.repository.store;

import com.example.ticketing.model.store.StoreCategoryMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreCategoryMappingRepository extends JpaRepository<StoreCategoryMapping, Long> {
    List<StoreCategoryMapping> findByStoreId(String storeId);
    List<StoreCategoryMapping> findByCategoryId(Long categoryId);
    void deleteByStoreId(String storeId);
}
