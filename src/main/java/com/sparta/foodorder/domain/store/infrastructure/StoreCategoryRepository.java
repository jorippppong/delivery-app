package com.sparta.foodorder.domain.store.infrastructure;

import com.sparta.foodorder.domain.store.domain.StoreCategory;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoreCategoryRepository extends JpaRepository<StoreCategory, UUID> {
    Page<StoreCategory> findAllByCategoryIdAndStoreIsActiveTrue(UUID categoryId, Pageable pageable);
}
