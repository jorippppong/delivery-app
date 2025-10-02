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

		// TODO: 3. requestDto.storeId()와 order.getStoreId() 일치 확인
		// if (!order.getStoreId().equals(requestDto.storeId())) {
		//     throw new BusinessException(ErrorCode.INVALID_STORE_ORDER);
		// }

		if (paymentRepository.existsByOrderId(requestDto.orderId())) {
			throw new BusinessException(ErrorCode.PAYMENT_ALREADY_EXISTS);
		}

		String pgTransactionId = "fake_toss_tx_" + UUID.randomUUID().toString().substring(0, 8);

		Payment payment = Payment.createPayment(
			userId,
			requestDto.orderId(),
			requestDto.amount(),
			requestDto.paymentMethod(),
			pgTransactionId
		);

		payment.completePayment();

		Payment savedPayment = paymentRepository.save(payment);

		// TODO: 8. Order 상태를 PAID로 변경
		// orderService.updateOrderStatus(requestDto.orderId(), OrderStatus.PAID);

		return PaymentResponseDto.from(savedPayment, requestDto.storeId());
	}

	@Override
	@Transactional
	public PaymentResponseDto refundPayment(UUID paymentId, PaymentRefundRequestDto requestDto, Long userId, String role) {

		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

		// TODO: storeId 조회를 위해 Order 조회 필요
		// Order order = orderService.getOrder(payment.getOrderId());
		// UUID storeId = order.getStoreId();
		UUID storeId = null;

		validateRefundPermission(payment, userId, role, storeId);

		if ("CUSTOMER".equals(role) && !payment.isRefundableWithinTimeLimit()) {
			throw new BusinessException(ErrorCode.PAYMENT_REFUND_TIME_EXPIRED);
		}

		if (!payment.isRefundableOrCancellable()) {
			throw new BusinessException(ErrorCode.PAYMENT_NOT_REFUNDABLE);
		}

		payment.refundPayment(requestDto.failReason());

		Payment savedPayment = paymentRepository.save(payment);

		// TODO: Order 상태를 REFUNDED로 변경
		// orderService.updateOrderStatus(payment.getOrderId(), OrderStatus.REFUNDED);

		return PaymentResponseDto.from(savedPayment, storeId);
	}

	@Override
	public PaymentResponseDto getPayment(UUID paymentId, Long userId, String role) {

		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

		// TODO: storeId 조회를 위해 Order 조회 필요
		// Order order = orderService.getOrder(payment.getOrderId());
		// Long storeId = order.getStoreId();
		UUID storeId = null;

		validateReadPermission(payment, userId, role, storeId);

		return PaymentResponseDto.from(payment, storeId);
	}

	private void validateRefundPermission(Payment payment, Long userId, String role, UUID storeId) {
		if ("MANAGER".equals(role) || "MASTER".equals(role)) {
			return;
		}

		if ("CUSTOMER".equals(role)) {
			if (!payment.getUserId().equals(userId)) {
				throw new BusinessException(ErrorCode.PAYMENT_USER_MISMATCH);
			}
			return;
		}

		if ("OWNER".equals(role)) {
			// TODO: 실제로는 Order를 통해 Store를 조회하여 ownerId 확인 필요
			// Order order = orderService.getOrder(payment.getOrderId());
			// Store store = storeService.getStore(order.getStoreId());
			// if (!store.getOwnerId().equals(userId)) {
			//     throw new BusinessException(ErrorCode.PAYMENT_OWNER_MISMATCH);
			// }
			return;
		}

		throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
	}

	private void validateReadPermission(Payment payment, Long userId, String role, UUID storeId) {
		if ("MANAGER".equals(role) || "MASTER".equals(role)) {
			return;
		}

		if ("CUSTOMER".equals(role)) {
			if (!payment.getUserId().equals(userId)) {
				throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
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
