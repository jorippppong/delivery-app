package com.sparta.foodorder.domain.payment.domain;

import com.sparta.foodorder.domain.payment.application.dto.PaymentCreateRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentRefundRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentResponseDto;

public interface PaymentService {

	PaymentResponseDto createPayment(PaymentCreateRequestDto paymentCreateRequestDto, Long userId);

	PaymentResponseDto refundPayment(Long paymentId, PaymentRefundRequestDto paymentRefundRequestDto, Long userId, String role);

	PaymentResponseDto getPayment(Long paymentId, Long userId, String role);
}
