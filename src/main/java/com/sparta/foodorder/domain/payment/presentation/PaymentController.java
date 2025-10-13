package com.sparta.foodorder.domain.payment.presentation;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.payment.application.dto.PaymentCreateRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentRefundRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentResponseDto;
import com.sparta.foodorder.domain.payment.domain.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/payments")
public class PaymentController {

	private final PaymentService paymentService;

	public PaymentController(PaymentService paymentService) {
		this.paymentService = paymentService;
	}

	@Operation(summary = "결제 생성", description = "새로운 결제를 생성합니다.")
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<PaymentResponseDto> createPayment(
		@Valid @RequestBody PaymentCreateRequestDto paymentCreateRequestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		PaymentResponseDto paymentResponseDto =
			paymentService.createPayment(paymentCreateRequestDto, userDetails.getUserId());

		return ResponseEntity.status(HttpStatus.CREATED).body(paymentResponseDto);
	}

	@Operation(summary = "결제 환불", description = "완료된 결제를 환불 처리합니다")
	@PreAuthorize("hasAnyRole('USER', 'OWNER', 'MANAGER', 'ADMIN')")
	@PostMapping("/{paymentId}/refund")
	public ResponseEntity<PaymentResponseDto> refundPayment(
		@PathVariable UUID paymentId,
		@Valid @RequestBody PaymentRefundRequestDto paymentRefundRequestDto,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		PaymentResponseDto paymentResponseDto =
			paymentService.refundPayment(paymentId, paymentRefundRequestDto, userDetails.getUserId(), userDetails.getUser().getRole().name());

		return ResponseEntity.ok(paymentResponseDto);
	}

	@Operation(summary = "결제 조회", description = "특정 결제 정보를 조회합니다")
	@PreAuthorize("hasAnyRole('USER', 'OWNER', 'MANAGER', 'ADMIN')")
	@GetMapping("/{paymentId}")
	public ResponseEntity<PaymentResponseDto> getPayment(
		@PathVariable UUID paymentId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		PaymentResponseDto paymentResponseDto = paymentService.getPayment(paymentId, userDetails.getUserId(), userDetails.getUser().getRole().name());

		return ResponseEntity.ok(paymentResponseDto);
	}
}
