package com.sparta.foodorder.domain.store.domain;

import java.util.*;

public interface CategoryRepository {

    List<Category> findByIdIn(Set<UUID> categoryIdList);
}
