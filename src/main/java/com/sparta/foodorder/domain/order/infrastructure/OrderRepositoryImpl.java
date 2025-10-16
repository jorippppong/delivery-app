package com.sparta.foodorder.domain.order.infrastructure;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    @Override
    public UUID save(Order order) {
        return orderJpaRepository.save(order).getId();
    }

    @Override
    public Page<Order> findAllByUserId(Long userId, Pageable pageable) {
        return orderJpaRepository.findAllByUserId(userId, pageable);
    }

    @Override
    public Page<Order> findAllByStoreId(UUID storeId, Pageable pageable) {
        return orderJpaRepository.findAllByStoreId(storeId, pageable);
    }
}
