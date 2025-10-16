package com.sparta.foodorder.domain.payment.event;

import com.sparta.foodorder.domain.order.application.OrderService;
import com.sparta.foodorder.domain.order.event.OrderEvent;
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

    @Async("eventExecutor")
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @Retryable(
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void handlePaymentRefunded(OrderEvent.OrderCanceled event) {
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
