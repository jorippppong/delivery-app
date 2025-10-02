package com.sparta.foodorder.domain.order.presentation.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GetUserOrdersResponseDto(
        UUID orderId,
        UUID storeId,
        String storeName,
        List<MenuInfo> menus,
        String status,
        int totalPrice,
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
