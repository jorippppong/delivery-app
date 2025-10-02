package com.sparta.foodorder.domain.payment.application;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderRepository;
import com.sparta.foodorder.domain.order.domain.OrderStatus;
import com.sparta.foodorder.domain.payment.application.dto.PaymentCreateRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentRefundRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentResponseDto;
import com.sparta.foodorder.domain.payment.domain.Payment;
import com.sparta.foodorder.domain.payment.domain.PaymentRepository;
import com.sparta.foodorder.domain.payment.domain.PaymentService;
import com.sparta.foodorder.domain.payment.event.PaymentEvent;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;

@Service
public class PaymentServiceImpl implements PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final StoreRepository storeRepository;
	private final ApplicationEventPublisher eventPublisher;

	public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository, StoreRepository storeRepository, ApplicationEventPublisher eventPublisher) {
		this.paymentRepository = paymentRepository;
		this.orderRepository = orderRepository;
		this.storeRepository = storeRepository;
		this.eventPublisher = eventPublisher;
	}

	@Override
	@Transactional
	public PaymentResponseDto createPayment(PaymentCreateRequestDto requestDto, Long userId) {

		Order order = orderRepository.findById(requestDto.orderId())
			.orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

		if (!order.getUserId().equals(userId)) {
			throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED, "본인의 주문만 결제할 수 있습니다.");
		}

		if (order.getOrderStatus() != OrderStatus.CREATED) {
			throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS, "결제 가능한 주문 상태가 아닙니다.");
		}

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

		eventPublisher.publishEvent(
			new PaymentEvent.PaymentCompleted(savedPayment.getOrderId(), savedPayment.getId())
		);

		return PaymentResponseDto.from(savedPayment , order.getStoreId());
	}

	@Override
	@Transactional
	public PaymentResponseDto refundPayment(UUID paymentId, PaymentRefundRequestDto requestDto, Long userId, String role) {

		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

		Order order = orderRepository.findById(payment.getOrderId())
			.orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

		validateRefundPermission(payment, order, userId, role);

		if ("CUSTOMER".equals(role) && !payment.isRefundableWithinTimeLimit()) {
			throw new BusinessException(ErrorCode.PAYMENT_REFUND_TIME_EXPIRED);
		}

		if (!payment.isRefundableOrCancellable()) {
			throw new BusinessException(ErrorCode.PAYMENT_NOT_REFUNDABLE);
		}

		payment.refundPayment(requestDto.failReason());

		Payment savedPayment = paymentRepository.save(payment);

		eventPublisher.publishEvent(
			new PaymentEvent.PaymentRefunded(
				payment.getOrderId(),
				savedPayment.getId(),
				requestDto.failReason()
			)
		);

		return PaymentResponseDto.from(savedPayment, order.getStoreId());
	}

	@Override
	public PaymentResponseDto getPayment(UUID paymentId, Long userId, String role) {

		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

		Order order = orderRepository.findById(payment.getOrderId())
			.orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

		validateReadPermission(payment, order, userId, role);

		return PaymentResponseDto.from(payment, order.getStoreId());
	}

	private void validateRefundPermission(Payment payment, Order order, Long userId, String role) {
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
			Store store = storeRepository.findById(order.getStoreId()).get();
			if (!store.getOwnerId().equals(userId)) {
				throw new BusinessException(ErrorCode.PAYMENT_OWNER_MISMATCH);
			}
			return;
		}

		throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
	}

	private void validateReadPermission(Payment payment, Order order, Long userId, String role) {
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
			Store store = storeRepository.findById(order.getStoreId()).get();
			if (!store.getOwnerId().equals(userId)) {
			    throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED,
			        "본인 가게의 주문만 조회할 수 있습니다.");
			return;
		}

		throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED, "권한이 없습니다.");
	}
}
