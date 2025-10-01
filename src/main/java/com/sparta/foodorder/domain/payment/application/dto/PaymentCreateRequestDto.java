package com.sparta.foodorder.domain.payment.application.dto;

import com.sparta.foodorder.domain.payment.domain.PaymentMethod;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PaymentCreateRequestDto (

	@NotNull(message = "가맹점 ID는 필수입니다.")
	Long storeId,

	@NotNull(message = "주문 ID는 필수입니다.")
	Long orderId,

	@NotNull(message = "결제 금액은 필수입니다.")
	@Min(value = 1, message = "결제 금액은 1원 이상이어야합니다.")
	Integer amount,

	PaymentMethod paymentMethod
) {
	 public PaymentCreateRequestDto {
		 if (paymentMethod == null) {
			 paymentMethod = PaymentMethod.CARD;
		 }
	 }
}
