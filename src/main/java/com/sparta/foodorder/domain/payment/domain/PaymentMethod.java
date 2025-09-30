package com.sparta.foodorder.domain.payment.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentMethod {

	CARD("카드 결제"),
	TOSS("토스페이");

	private final String description;
}
