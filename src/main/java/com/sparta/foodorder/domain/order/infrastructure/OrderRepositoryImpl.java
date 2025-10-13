package com.sparta.foodorder.domain.order.infrastructure;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId);
    }
}
