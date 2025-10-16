package com.sparta.foodorder.domain.store.infrastructure;

import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {

    private final StoreJpaRepository jpaRepository;

    @Override
    public boolean existsByNameAndIsActiveTrue(String name) {
        return jpaRepository.existsByNameAndIsActiveTrue(name);
    }

    @Override
    public boolean existsByOwnerIdAndIsActiveTrue(Long ownerId) {
        return jpaRepository.existsByOwnerIdAndIsActiveTrue(ownerId);
    }

    @Override
    public boolean existsByPhoneNumberAndIsActiveTrue(String phoneNumber) {
        return jpaRepository.existsByPhoneNumberAndIsActiveTrue(phoneNumber);
    }

    @Override
    public Store save(Store store) {
        return jpaRepository.save(store);
    }

    @Override
    public Optional<Store> findByIdAndIsActiveTrue(UUID id) {
        return jpaRepository.findByIdAndIsActiveTrue(id);
    }

    @Override
    public Optional<Store> findByIdAndDeletedAtIsNull(UUID id) {
        return jpaRepository.findByIdAndDeletedAtIsNull(id);
    }

    @Override
    public Optional<Store> findById(UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public boolean existsByIdAndIsActiveTrue(UUID id) {
        return jpaRepository.existsByIdAndIsActiveTrue(id);
    }

    @Override
    public Page<Store> findAllByIsActiveTrue(Pageable pageable) {
        return jpaRepository.findAllByIsActiveTrue(pageable);
    }

    @Override
    public Page<Store> findAllByNameContainingIgnoreCaseAndIsActiveTrue(String q, Pageable pageable) {
        return jpaRepository.findAllByNameContainingIgnoreCaseAndIsActiveTrue(q, pageable);
    }

    @Override
    public Page<Store> findAllByDeletedAtIsNull(Pageable pageable) {
        return jpaRepository.findAllByDeletedAtIsNull(pageable);
    }

    @Override
    public Page<Store> findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(String q, Pageable pageable) {
        return jpaRepository.findAllByNameContainingIgnoreCaseAndDeletedAtIsNull(q, pageable);
    }

    @Override
    public Page<Store> findAllByIsActiveTrueAndStoreCategoriesCategoryId(UUID categoryId, Pageable pageable) {
        return jpaRepository.findAllByIsActiveTrueAndStoreCategories_Category_Id(categoryId, pageable);
    }

    @Override
    public Page<Store> findAllByDeletedAtIsNullAndStoreCategoriesCategoryId(UUID categoryId, Pageable pageable) {
        return jpaRepository.findAllByDeletedAtIsNullAndStoreCategories_Category_Id(categoryId, pageable);
    }

    @Override
    public Page<Store> findAllByNameContainingAndIsActiveTrue(String q, Pageable pageable) {
        return null; // TODO
    }

    @Override
    public Optional<Store> findByOwnerId(Long ownerId) {
        return jpaRepository.findByOwnerId(ownerId);
    }

    @Override
    public List<Store> findAllByIdIn(Set<UUID> storeIds) {
        return jpaRepository.findAllByIdIn(storeIds);
    }
}
