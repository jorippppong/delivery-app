package com.sparta.foodorder.domain.order.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GetOrderResponseDto(
        String customerNickname,
        String deliveryAddress,
        List<MenuInfo> menus,
        String status,
        int totalAmount,
        LocalDateTime createdAt
) {
    private record MenuInfo(
            UUID menuId,
            String menuName,
            int quantity,
            List<OptionInfo> options
    ) {

    }

    private record OptionInfo(
            String optionName,
            List<String> optionValues
    ) {

    }
}

