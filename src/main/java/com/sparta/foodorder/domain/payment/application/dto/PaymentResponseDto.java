package com.sparta.foodorder.domain.payment.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.foodorder.domain.payment.domain.Payment;
import com.sparta.foodorder.domain.payment.domain.PaymentMethod;
import com.sparta.foodorder.domain.payment.domain.PaymentStatus;

public record PaymentResponseDto (
	UUID id,
	Long userId,
	UUID storeId,
	UUID orderId,
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

	public static PaymentResponseDto from(Payment payment, UUID storeId) {
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
