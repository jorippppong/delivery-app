package com.sparta.foodorder.domain.order.infrastructure;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderMenu;
import com.sparta.foodorder.domain.order.domain.OrderMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderMenuRepositoryImpl implements OrderMenuRepository {
    private final OrderMenuJpaRepository orderMenuJpaRepository;

    @Override
    public void save(OrderMenu orderMenu) {
        orderMenuJpaRepository.save(orderMenu);
    }

    @Override
    public List<OrderMenu> findAllByOrder(Order order) {
        return orderMenuJpaRepository.findAllByOrder(order);
    }
}
