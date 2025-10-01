package com.sparta.foodorder.domain.payment.domain;

import java.util.Optional;

public interface PaymentRepository {

	Payment save(Payment payment);

	Optional<Payment> findById(Long id);

	boolean existsByOrderId(Long OrderId);
}
