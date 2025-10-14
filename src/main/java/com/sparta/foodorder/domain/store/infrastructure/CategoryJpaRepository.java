package com.sparta.foodorder.domain.store.infrastructure;

import com.sparta.foodorder.domain.store.domain.Category;
import java.util.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryJpaRepository extends JpaRepository<Category, UUID> {

    List<Category> findByIdIn(Set<UUID> categoryIdList);

    Boolean existsByName(String name);
}
