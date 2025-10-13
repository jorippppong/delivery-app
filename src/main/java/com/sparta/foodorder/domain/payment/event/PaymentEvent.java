package com.sparta.foodorder.domain.payment.event;

import java.util.UUID;

public class PaymentEvent {

	public record PaymentCompleted(
		UUID orderId,
		UUID paymentId
	) {}

	public record PaymentRefunded(
		UUID orderId,
		UUID paymentId,
		String reason
	) {}
}