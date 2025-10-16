package com.sparta.foodorder.domain.payment.application;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderRepository;
import com.sparta.foodorder.domain.order.domain.OrderStatus;
import com.sparta.foodorder.domain.order.event.OrderEvent;
import com.sparta.foodorder.domain.payment.application.dto.PaymentCreateRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentRefundRequestDto;
import com.sparta.foodorder.domain.payment.application.dto.PaymentResponseDto;
import com.sparta.foodorder.domain.payment.domain.Payment;
import com.sparta.foodorder.domain.payment.domain.PaymentRepository;
import com.sparta.foodorder.domain.payment.domain.PaymentService;
import com.sparta.foodorder.domain.payment.event.PaymentEvent;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreRepository;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final StoreRepository storeRepository;
    private final ApplicationEventPublisher eventPublisher;

    public PaymentServiceImpl(PaymentRepository paymentRepository, OrderRepository orderRepository,
                              StoreRepository storeRepository, ApplicationEventPublisher eventPublisher) {
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
            throw new BusinessException(ErrorCode.PAYMENT_USER_MISMATCH);
        }

        if (order.getOrderStatus() != OrderStatus.CREATED) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
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

        return PaymentResponseDto.from(savedPayment, order.getStoreId());
    }

    @Override
    @Transactional
    public PaymentResponseDto refundPayment(UUID paymentId, PaymentRefundRequestDto requestDto, Long userId, UserRole role) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        validatePermission(payment, order, userId, role);

        if (role == UserRole.USER && !payment.isRefundableWithinTimeLimit()) {
            throw new BusinessException(ErrorCode.PAYMENT_REFUND_TIME_EXPIRED);
        }

        if (!payment.isRefundableOrCancellable()) {
            throw new BusinessException(ErrorCode.PAYMENT_NOT_REFUNDABLE);
        }

        payment.refundPayment(requestDto.failReason());

        Payment savedPayment = paymentRepository.save(payment);

        eventPublisher.publishEvent(
                new OrderEvent.OrderCanceled(
                        payment.getOrderId(),
                        savedPayment.getId(),
                        requestDto.failReason()
                )
        );

        return PaymentResponseDto.from(savedPayment, order.getStoreId());
    }

    @Override
    public PaymentResponseDto getPayment(UUID paymentId, Long userId, UserRole role) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        Order order = orderRepository.findById(payment.getOrderId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        validatePermission(payment, order, userId, role);

        return PaymentResponseDto.from(payment, order.getStoreId());
    }

    @Override
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    private void validatePermission(Payment payment, Order order, Long userId, UserRole role) {
        switch (role) {
            case MANAGER, MASTER -> {
            }
            case USER -> validateUserPermission(payment, userId);
            case OWNER -> validateOwnerPermission(order, userId);
            default -> throw new BusinessException(ErrorCode.PAYMENT_ACCESS_DENIED);
        }
    }

    private void validateUserPermission(Payment payment, Long userId) {
        if (!payment.getUserId().equals(userId)) {
            throw new BusinessException(ErrorCode.PAYMENT_USER_MISMATCH);
        }
    }

    private void validateOwnerPermission(Order order, Long userId) {
        Store store = storeRepository.findById(order.getStoreId())
                .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getOwnerId().equals(userId)) {
            throw new BusinessException(ErrorCode.PAYMENT_OWNER_MISMATCH);
        }
    }
}
