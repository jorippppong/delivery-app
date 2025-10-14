package com.sparta.foodorder.domain.store.domain;

import java.util.*;

public interface CategoryRepository {

    List<Category> findByIdIn(Set<UUID> categoryIdList);

    Optional<Category> findById(UUID categoryId);

    Boolean existsByName(String name);

    Category save(Category category);
}
