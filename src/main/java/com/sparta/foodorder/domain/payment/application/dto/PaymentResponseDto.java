package com.sparta.foodorder.domain.payment.application.dto;

import java.time.LocalDateTime;

import com.sparta.foodorder.domain.payment.domain.Payment;
import com.sparta.foodorder.domain.payment.domain.PaymentMethod;
import com.sparta.foodorder.domain.payment.domain.PaymentStatus;

public record PaymentResponseDto (
	Long id,
	Long userId,
	Long storeId,
	Long orderId,
	Integer amount,
	PaymentMethod paymentMethod,
	PaymentStatus status,
	LocalDateTime createdAt,
	LocalDateTime updatedAt,
	PaymentDetailDto detail
) {

	public record PaymentDetailDto (
		String pgTransactionId,
		String failReason,
		LocalDateTime paidAt,
		LocalDateTime failedAt,
		LocalDateTime refundedAt
	) {
	}

	public static PaymentResponseDto from(Payment payment, Long storeId) {
		return new PaymentResponseDto(
			payment.getId(),
			payment.getUserId(),
			storeId,
			payment.getOrderId(),
			payment.getAmount(),
			payment.getPaymentMethod(),
			payment.getStatus(),
			payment.getCreatedAt(),
			payment.getUpdatedAt(),
			new PaymentDetailDto(
				payment.getPgTransactionId(),
				payment.getFailReason(),
				payment.getPaidAt(),
				payment.getFailedAt(),
				payment.getRefundedAt()
			)
		);
	}
}
