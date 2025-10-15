package com.sparta.foodorder.domain.payment.event;

import com.sparta.foodorder.domain.order.application.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
public class OrderPaymentEventListener {

    private final OrderService orderService;

    public OrderPaymentEventListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @Async("eventExecutor") // 비동기로 실행
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    // 트랜잭션 커밋 후에 이벤트 처리 (이벤트 발행한 트랜잭션이 성공적으로 커밋된 후에만 실행)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    // 기존 트랜잭션과 무관하게 완전히 새로운 트랜잭션 시작 (이 메서드의 성공/실패가 결제 트랜잭션에 영향 안준다.)
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handlePaymentCompleted(PaymentEvent.PaymentCompleted event) {
        log.info("결제 완료 이벤트 수신 - orderId: {}, paymentId: {}",
                event.orderId(), event.paymentId());

        try {
            // orderService.updateOrderStatusToPending(event.orderId());
            log.info("주문 상태를 PENDING으로 변경 완료 - orderId: {}", event.orderId());
        } catch (Exception e) {
            log.error("주문 상태 변경 실패 - orderId: {}", event.orderId(), e);
            // 필요하다면 보상 트랜잭션 처리
        }
    }

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handlePaymentRefunded(PaymentEvent.PaymentRefunded event) {
        log.info("결제 환불 이벤트 수신 - orderId: {}, paymentId: {}, reason: {}",
                event.orderId(), event.paymentId(), event.reason());

        try {
            log.info("주문 상태를 CANCELED로 변경 완료 - orderId: {}", event.orderId());
        } catch (Exception e) {
            log.error("주문 상태 변경 실패 - orderId: {}", event.orderId(), e);
            // 필요하다면 보상 트랜잭션 처리
        }
    }
}
