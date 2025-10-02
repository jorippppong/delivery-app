package com.sparta.foodorder.domain.order.domain;

import java.util.UUID;

public interface OrderRepository {
    Order findById(UUID orderId);
}
