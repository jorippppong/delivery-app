package com.sparta.foodorder.domain.order.infrastructure;

import com.sparta.foodorder.domain.order.domain.OrderMenu;
import com.sparta.foodorder.domain.order.domain.OrderMenuOption;
import com.sparta.foodorder.domain.order.domain.OrderMenuOptionValue;
import com.sparta.foodorder.domain.order.domain.OrderMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderMenuImplRepository implements OrderMenuRepository {
    private final OrderMenuJpaRepository orderMenuJpaRepository;
    private final OrderMenuOptionJpaRepository orderMenuOptionJpaRepository;
    private final OrderMenuOptionValueJpaRepository orderMenuOptionValueJpaRepository;

    @Override
    public void saveAllOrderMenu(List<OrderMenu> orderMenus) {
        orderMenuJpaRepository.saveAll(orderMenus);
    }

    @Override
    public void saveAllOrderMenuOption(List<OrderMenuOption> orderMenuOptions) {
        orderMenuOptionJpaRepository.saveAll(orderMenuOptions);
    }

    @Override
    public void saveAllOrderMenuOptionValue(List<OrderMenuOptionValue> orderMenuOptionValues) {
        orderMenuOptionValueJpaRepository.saveAll(orderMenuOptionValues);
    }
}
