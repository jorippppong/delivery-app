package com.sparta.foodorder.domain.menu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {

    List<Menu> findByStoreId(UUID storeId);

    Optional<Menu> findById(UUID id);

    Menu save(Menu menu);

    void delete(Menu menu);

    //메뉴 전체 조회(일반 유저용)
    List<Menu> findByStoreIdAndActiveTrueAndHiddenFalseAndDeletedAtIsNull(UUID storeId);
    //메뉴 전체 조회(가게 주인용)
    List<Menu> findByStoreIdAndDeletedAtIsNull(UUID storeId);
}
