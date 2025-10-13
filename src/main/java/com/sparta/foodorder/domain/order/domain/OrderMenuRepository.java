package com.sparta.foodorder.domain.order.domain;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMenuRepository {
    void saveAll(List<OrderMenu> orderMenus);
}
