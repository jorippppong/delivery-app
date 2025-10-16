package com.sparta.foodorder.domain.payment.infrastructure;

import com.sparta.foodorder.domain.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

    boolean existsByOrderId(UUID orderId);

    Optional<Payment> findById(UUID id);

    Optional<Payment> findByOrderId(UUID orderId);
}
