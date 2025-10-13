package com.sparta.foodorder.domain.payment.application.dto;

import jakarta.validation.constraints.NotBlank;

public record PaymentRefundRequestDto (

	@NotBlank(message = "환불 사유는 필수입니다.")
	String failReason
) {
}
