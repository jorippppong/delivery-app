package com.sparta.foodorder.domain.store.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreRepository {
    boolean existsByName(String name);

    boolean existsByOwnerId(Long ownerId);

    Store save(Store store);

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<Store> findById(UUID id);

    boolean existsByIdAndIsActiveTrue(UUID id);

    Page<Store> findAllByIsActiveTrue(Pageable pageable);

    Page<Store> findAllByNameContainingAndIsActiveTrue(String q, Pageable pageable);

    Optional<Store> findByOwnerId(Long ownerId);

}
