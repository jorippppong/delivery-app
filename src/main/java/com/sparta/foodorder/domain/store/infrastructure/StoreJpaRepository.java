package com.sparta.foodorder.domain.store.infrastructure;

import com.sparta.foodorder.domain.store.domain.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreJpaRepository extends JpaRepository<Store, UUID> {
    boolean existsByNameAndIsActiveTrue(String name);

    boolean existsByOwnerIdAndIsActiveTrue(Long ownerId);

    boolean existsByPhoneNumberAndIsActiveTrue(String phoneNumber);

    Optional<Store> findById(UUID id);

    Optional<Store> findByIdAndIsActiveTrue(UUID id);

    Optional<Store> findByIdAndDeletedAtIsNull(UUID id);

    boolean existsByIdAndIsActiveTrue(UUID id);

    Page<Store> findAllByIsActiveTrue(Pageable pageable);

    Page<Store> findAllByNameContainingIgnoreCaseAndIsActiveTrue(String q, Pageable pageable);

    Page<Store> findAllByDeletedAtIsNull(Pageable pageable);

    Page<Store> findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(String query, Pageable pageable);

    Page<Store> findAllByIsActiveTrueAndStoreCategories_Category_Id(UUID categoryId, Pageable pageable);

    Page<Store> findAllByDeletedAtIsNullAndStoreCategories_Category_Id(UUID categoryId, Pageable pageable);

    Optional<Store> findByOwnerId(Long ownerId);

}
