package com.sparta.foodorder.domain.store.infrastructure;

import com.sparta.foodorder.domain.store.domain.Store;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface StoreJpaRepository extends JpaRepository<Store, UUID> {
    boolean existsByName(String name);
    boolean existsByOwnerId(Long ownerId);
    boolean existsByPhoneNumber(String phoneNumber);
    Optional<Store> findById(UUID id);
}
