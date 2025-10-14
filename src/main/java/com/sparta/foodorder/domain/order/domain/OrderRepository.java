package com.sparta.foodorder.domain.order.domain;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> findById(UUID orderId);

    UUID save(Order order);
}
