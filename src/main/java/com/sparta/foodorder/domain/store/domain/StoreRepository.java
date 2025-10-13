package com.sparta.foodorder.domain.store.domain;

import java.util.Optional;
import java.util.UUID;

public interface StoreRepository {
    boolean existsByName(String name);

    boolean existsByOwnerId(Long ownerId);

    Store save(Store store);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Store> findById(UUID id);
}
