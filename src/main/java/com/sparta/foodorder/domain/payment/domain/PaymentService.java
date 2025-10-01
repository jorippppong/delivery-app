package com.sparta.foodorder.domain.payment.domain;

import java.util.UUID;

import com.sparta.foodorder.domain.payment.application.dto.PaymentCreateRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentRefundRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentResponseDto;

public interface PaymentService {

	PaymentResponseDto createPayment(PaymentCreateRequestDto paymentCreateRequestDto, Long userId);

	PaymentResponseDto refundPayment(UUID paymentId, PaymentRefundRequestDto paymentRefundRequestDto, Long userId, String role);

	PaymentResponseDto getPayment(UUID paymentId, Long userId, String role);
}
