package com.sparta.foodorder.domain.order.event;

import java.util.UUID;

public class OrderEvent {

    public record OrderCanceled(
            UUID orderId,
            UUID paymentId,
            String reason
    ) {
    }
}
