package com.sparta.foodorder.domain.order.infrastructure;

import com.sparta.foodorder.domain.order.domain.OrderMenu;
import com.sparta.foodorder.domain.order.domain.OrderMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderMenuImplRepository implements OrderMenuRepository {
    private final OrderMenuJpaRepository orderMenuJpaRepository;

    @Override
    public void saveAll(List<OrderMenu> orderMenus) {
        orderMenuJpaRepository.saveAll(orderMenus);
    }
}
