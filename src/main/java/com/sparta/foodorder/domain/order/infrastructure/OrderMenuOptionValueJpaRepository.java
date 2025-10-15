package com.sparta.foodorder.domain.order.infrastructure;

import com.sparta.foodorder.domain.order.domain.OrderMenuOptionValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderMenuOptionValueJpaRepository extends JpaRepository<OrderMenuOptionValue, Long> {
}
