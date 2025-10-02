package com.sparta.foodorder.domain.store.domain;

import java.util.UUID;

public interface StoreService {

    Store findById(UUID storeId);
}
