package com.sparta.foodorder.domain.store.domain;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StoreRepository {
    boolean existsByNameAndIsActiveTrue(String name);

    boolean existsByOwnerIdAndIsActiveTrue(Long ownerId);

    Store save(Store store);

    Optional<Store> findByIdAndIsActiveTrue(UUID id);

    Optional<Store> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByPhoneNumberAndIsActiveTrue(String phoneNumber);

    Optional<Store> findById(UUID id);

    boolean existsByIdAndIsActiveTrue(UUID id);

    Page<Store> findAllByIsActiveTrue(Pageable pageable);

    Page<Store> findAllByNameContainingIgnoreCaseAndIsActiveTrue(String query, Pageable pageable);

    Page<Store> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Store> findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(String query, Pageable pageable);

    Page<Store> findAllByIsActiveTrueAndStoreCategoriesCategoryId(UUID categoryId, Pageable pageable);

    Page<Store> findAllByDeletedAtIsNullAndStoreCategoriesCategoryId(UUID categoryId, Pageable pageable);

    Optional<Store> findByOwnerId(Long ownerId);

}
