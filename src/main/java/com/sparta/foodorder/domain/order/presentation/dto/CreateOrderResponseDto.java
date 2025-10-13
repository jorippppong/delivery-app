package com.sparta.foodorder.domain.order.presentation.dto;

import java.util.UUID;

public record CreateOrderResponseDto(
        UUID orderId
) {
}
