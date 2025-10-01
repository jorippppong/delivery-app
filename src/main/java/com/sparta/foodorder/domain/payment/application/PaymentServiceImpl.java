package com.sparta.foodorder.domain.payment.application;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.foodorder.domain.payment.application.dto.PaymentCreateRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentRefundRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentResponseDto;
import com.sparta.foodorder.domain.payment.domain.Payment;
import com.sparta.foodorder.domain.payment.domain.PaymentRepository;
import com.sparta.foodorder.domain.payment.domain.PaymentService;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;

	public PaymentServiceImpl(PaymentRepository paymentRepository) {
		this.paymentRepository = paymentRepository;
	}

	@Override
	@Transactional
	public PaymentResponseDto createPayment(PaymentCreateRequestDto requestDto, Long userId) {
		// TODO: 1. 주문 존재 여부 및 소유권 확인
		// Order order = orderService.getOrder(requestDto.orderId());
		// if (!order.getUserId().equals(userId)) {
		//     throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
		// }

		// TODO: 2. 주문 상태가 결제 가능한 상태인지 확인
		// if (!order.isPayable()) {
		//     throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
		// }

		// 3. 중복 결제 방지
		if (paymentRepository.existsByOrderId(requestDto.orderId())) {
			throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
		}

		// 4. Fake PG 거래 ID 생성 (실제로는 PG사 API 호출)
		String pgTransactionId = "fake_toss_tx_" + UUID.randomUUID().toString().substring(0, 8);

		// 5. 결제 엔티티 생성
		Payment payment = Payment.createPayment(
			userId,
			requestDto.orderId(),
			requestDto.amount(),
			requestDto.paymentMethod(),
			pgTransactionId
		);

		// 6. 결제 완료 처리 (테스트 환경이므로 바로 완료)
		payment.completePayment();

		Payment savedPayment = paymentRepository.save(payment);

		// TODO: 7. Order 상태를 PAID로 변경
		// orderService.updateOrderStatus(requestDto.orderId(), OrderStatus.PAID);

		return PaymentResponseDto.from(savedPayment, requestDto.storeId());
	}

	@Override
	@Transactional
	public PaymentResponseDto refundPayment(Long paymentId, PaymentRefundRequestDto requestDto, Long userId, String role) {
		// 1. 결제 조회
		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

		// TODO: 2. storeId 조회를 위해 Order 조회 필요
		// Order order = orderService.getOrder(payment.getOrderId());
		// Long storeId = order.getStoreId();
		Long storeId = null; // TODO: Order에서 가져오기

		// 3. 권한 확인
		validateRefundPermission(payment, userId, role, storeId);

		// 4. 환불 가능 상태 확인
		if (!payment.isRefundableOrCancellable()) {
			throw new BusinessException(ErrorCode.PAYMENT_NOT_REFUNDABLE,
				"현재 결제 상태: " + payment.getStatus());
		}

		// 5. 환불 처리
		payment.refundPayment(requestDto.failReason());

		Payment savedPayment = paymentRepository.save(payment);

		// TODO: 6. Order 상태를 REFUNDED로 변경
		// orderService.updateOrderStatus(payment.getOrderId(), OrderStatus.REFUNDED);

		return PaymentResponseDto.from(savedPayment, storeId);
	}

	@Override
	public PaymentResponseDto getPayment(Long paymentId, Long userId, String role) {
		// 1. 결제 조회
		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

		// TODO: 2. storeId 조회를 위해 Order 조회 필요
		// Order order = orderService.getOrder(payment.getOrderId());
		// Long storeId = order.getStoreId();
		Long storeId = null; // TODO: Order에서 가져오기

		// 3. 권한 확인
		validateReadPermission(payment, userId, role, storeId);

		return PaymentResponseDto.from(payment, storeId);
	}

	private void validateRefundPermission(Payment payment, Long userId, String role, Long storeId) {
		if ("MANAGER".equals(role) || "MASTER".equals(role)) {
			return;
		}

		if ("CUSTOMER".equals(role)) {
			if (!payment.getUserId().equals(userId)) {
				throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED,
					"본인의 결제만 환불할 수 있습니다.");
			}
			return;
		}

		if ("OWNER".equals(role)) {
			// TODO: 실제로는 Order를 통해 Store를 조회하여 owner 확인 필요
			// Store store = storeService.getStore(storeId);
			// if (!store.getOwnerId().equals(userId)) {
			//     throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED,
			//         "본인 가게의 주문만 환불할 수 있습니다.");
			// }
			return;
		}

		throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED, "권한이 없습니다.");
	}

	private void validateReadPermission(Payment payment, Long userId, String role, Long storeId) {
		if ("MANAGER".equals(role) || "MASTER".equals(role)) {
			return;
		}

		if ("CUSTOMER".equals(role)) {
			if (!payment.getUserId().equals(userId)) {
				throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED,
					"본인의 결제만 조회할 수 있습니다.");
			}
			return;
		}

		if ("OWNER".equals(role)) {
			// TODO: 실제로는 Order를 통해 Store를 조회하여 owner 확인 필요
			// Store store = storeService.getStore(storeId);
			// if (!store.getOwnerId().equals(userId)) {
			//     throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED,
			//         "본인 가게의 주문만 조회할 수 있습니다.");
			// }
			return;
		}

		throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED, "권한이 없습니다.");
	}
}
