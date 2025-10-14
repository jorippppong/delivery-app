package com.sparta.foodorder.domain.menu.domain;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MenuRepository {

    List<Menu> findByStoreId(UUID storeId);

    Optional<Menu> findById(UUID id);

    Menu save(Menu menu);

    void delete(Menu menu);

    List<Menu> findAllByIds(List<UUID> menuIds);

}
