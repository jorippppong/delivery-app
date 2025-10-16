package com.sparta.foodorder.domain.order.infrastructure;

import com.sparta.foodorder.domain.order.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, UUID> {
    Optional<Order> findById(UUID id);

    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    Page<Order> findAllByStoreId(UUID storeId, Pageable pageable);
}
