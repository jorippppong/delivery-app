package com.sparta.foodorder.domain.store.infrastructure;

import com.sparta.foodorder.domain.store.domain.Category;
import com.sparta.foodorder.domain.store.domain.CategoryRepository;
import java.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CategoryRepositoryImpl implements CategoryRepository {
    private final CategoryJpaRepository jpaRepository;

    @Override
    public List<Category> findByIdIn(Set<UUID> categoryIdList) {
        return jpaRepository.findByIdIn(categoryIdList);
    }

    @Override
    public Optional<Category> findById(UUID categoryId) {
        return jpaRepository.findById(categoryId);
    }

    @Override
    public Boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public Category save(Category category) {
        return jpaRepository.save(category);
    }
}
