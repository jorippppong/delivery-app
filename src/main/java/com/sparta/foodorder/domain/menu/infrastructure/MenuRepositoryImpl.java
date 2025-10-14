package com.sparta.foodorder.domain.menu.infrastructure;

import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.menu.domain.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@RequiredArgsConstructor
@Repository
public class MenuRepositoryImpl implements MenuRepository {

    private final MenuJpaRepository menuJpaRepository;

    @Override
    public List<Menu> findByStoreId(UUID storeId) {
        return menuJpaRepository.findByStoreId(storeId);
    }

    @Override
    public Optional<Menu> findById(UUID id) {
        return menuJpaRepository.findById(id);
    }

    @Override
    public Menu save(Menu menu) {
        return menuJpaRepository.save(menu);
    }

    @Override
    public void delete(Menu menu) {
        menuJpaRepository.delete(menu);
    }

    @Override
    public List<Menu> findAllByIds(List<UUID> menuIds) {
        return menuJpaRepository.findAllById(menuIds);
    }

}
