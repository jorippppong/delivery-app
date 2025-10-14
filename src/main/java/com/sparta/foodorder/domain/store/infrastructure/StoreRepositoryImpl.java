package com.sparta.foodorder.domain.store.infrastructure;

import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepository {

    private final StoreJpaRepository jpaRepository;

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    @Override
    public boolean existsByOwnerId(Long ownerId) {
        return jpaRepository.existsByOwnerId(ownerId);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return jpaRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public Store save(Store store) {
        return jpaRepository.save(store);
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
    public Page<Store> findAllByNameContainingAndIsActiveTrue(String q, Pageable pageable) {
        return jpaRepository.findAllByNameContainingAndIsActiveTrue(q, pageable);
    }
}
