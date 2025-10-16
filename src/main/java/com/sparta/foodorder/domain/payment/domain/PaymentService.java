package com.sparta.foodorder.domain.payment.domain;

import com.sparta.foodorder.domain.payment.application.dto.PaymentCreateRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentRefundRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentResponseDto;
import com.sparta.foodorder.domain.user.domain.UserRole;

import java.util.Optional;
import java.util.UUID;

public interface PaymentService {

    PaymentResponseDto createPayment(PaymentCreateRequestDto paymentCreateRequestDto, Long userId);

    PaymentResponseDto refundPayment(UUID paymentId, PaymentRefundRequestDto paymentRefundRequestDto, Long userId, UserRole role);

    PaymentResponseDto getPayment(UUID paymentId, Long userId, UserRole role);

    Optional<Payment> findByOrderId(UUID orderId);
}
