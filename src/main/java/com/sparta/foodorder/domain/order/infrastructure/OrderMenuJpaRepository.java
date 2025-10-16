package com.sparta.foodorder.domain.order.infrastructure;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderMenu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderMenuJpaRepository extends JpaRepository<OrderMenu, Long> {
    List<OrderMenu> findAllByOrder(Order order);
}
