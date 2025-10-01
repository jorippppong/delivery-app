package com.sparta.foodorder.domain.payment.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sparta.foodorder.domain.payment.domain.Payment;

public interface PaymentJpaRepository extends JpaRepository<Payment, Long> {
}
