package com.sparta.foodorder.domain.payment.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.foodorder.domain.payment.domain.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {

	boolean existsByOrderId(Long orderId);

	Optional<Payment> findByPaymentPublicId(UUID paymentPublicId);
}
