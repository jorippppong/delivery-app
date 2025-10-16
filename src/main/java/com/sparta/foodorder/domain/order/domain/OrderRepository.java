package com.sparta.foodorder.domain.order.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> findById(UUID orderId);

    UUID save(Order order);

    Page<Order> findAllByUserId(Long userId, Pageable pageable);

    Page<Order> findAllByStoreId(UUID storeId, Pageable pageable);
}
