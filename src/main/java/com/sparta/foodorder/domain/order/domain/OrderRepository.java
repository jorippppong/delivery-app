package com.sparta.foodorder.domain.order.domain;

import com.sparta.foodorder.domain.order.application.OrderDetailQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Optional<Order> findById(UUID orderId);

    UUID save(Order order);

    OrderDetailQuery getOrderDetail(UUID orderId);

    List<OrderDetailQuery> getUserOrders(Long userId, int page, int size);

    List<OrderDetailQuery> getStoreOrders(UUID storeId, int page, int size);
}
