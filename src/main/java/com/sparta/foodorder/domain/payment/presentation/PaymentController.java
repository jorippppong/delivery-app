package com.sparta.foodorder.domain.payment.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.foodorder.domain.payment.application.dto.PaymentCreateRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentRefundRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentResponseDto;
import com.sparta.foodorder.domain.payment.domain.PaymentService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@PostMapping
	public ResponseEntity<PaymentResponseDto> createPayment(
		@Valid @RequestBody PaymentCreateRequestDto paymentCreateRequestDto
	) {
		// TODO: 인증 구현 후 실제 userId 사용
		Long userId = 1L;

		PaymentResponseDto paymentResponseDto = paymentService.createPayment(paymentCreateRequestDto, userId);

		return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
	}

	@PostMapping("/{paymentId}/refund")
	public ResponseEntity<PaymentResponseDto> refundPayment(
		@PathVariable UUID paymentId,
		@Valid @RequestBody PaymentRefundRequestDto paymentRefundRequestDto
	) {
		// TODO: 인증 구현 후 실제 userId, role 사용
		Long userId = 1L;
		String role = "CUSTOMER";

		PaymentResponseDto paymentResponseDto = paymentService.refundPayment(paymentId, paymentRefundRequestDto, userId, role);

		return ResponseEntity.ok(paymentResponseDto);
	}

	@GetMapping("/{paymentId}")
	public ResponseEntity<PaymentResponseDto> getPayment(
		@PathVariable UUID paymentId
	) {
		Long userId = 1L;
		String role = "CUSTOMER";

		PaymentResponseDto paymentResponseDto = paymentService.getPayment(paymentId, userId, role);

		return ResponseEntity.ok(paymentResponseDto);
	}
}
