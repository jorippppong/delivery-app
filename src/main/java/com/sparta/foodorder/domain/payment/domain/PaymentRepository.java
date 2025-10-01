package com.sparta.foodorder.domain.payment.domain;

import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository {

	Payment save(Payment payment);

	Optional<Payment> findById(Long id);

	Optional<Payment> findByPaymentPublicId(UUID paymentPublicId);

	boolean existsByOrderId(Long OrderId);
}
