package com.sparta.foodorder.domain.order.domain;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMenuRepository {
    void save(OrderMenu orderMenu);

    List<OrderMenu> findAllByOrder(Order order);
}
