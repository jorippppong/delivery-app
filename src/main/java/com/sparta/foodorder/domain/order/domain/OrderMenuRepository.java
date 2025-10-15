package com.sparta.foodorder.domain.order.domain;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMenuRepository {
    void saveAllOrderMenu(List<OrderMenu> orderMenus);

    void saveAllOrderMenuOption(List<OrderMenuOption> orderMenuOptions);

    void saveAllOrderMenuOptionValue(List<OrderMenuOptionValue> orderMenuOptionValues);
}
