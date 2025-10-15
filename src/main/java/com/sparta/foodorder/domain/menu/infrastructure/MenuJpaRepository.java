package com.sparta.foodorder.domain.menu.infrastructure;

import com.sparta.foodorder.domain.menu.domain.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MenuJpaRepository extends JpaRepository<Menu, UUID> {
    List<Menu> findByStoreId(UUID storeId);
    List<Menu> findByStoreIdAndDeletedAtIsNull(UUID storeId);
    List<Menu> findByStoreIdAndActiveTrueAndHiddenFalse(UUID storeId);
    List<Menu> findByStoreIdAndActiveTrueAndHiddenFalseAndDeletedAtIsNull(UUID storeId);
    Page<Menu> findByNameContaining(String searchString, Pageable pageable);

}
