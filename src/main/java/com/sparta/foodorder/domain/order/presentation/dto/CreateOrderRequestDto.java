package com.sparta.foodorder.domain.order.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequestDto(
        @NotNull
        UUID storeId,
        List<MenuInfo> menus,
        @NotBlank
        String deliveryAddress,
        String memo
) {
    private record MenuInfo(
            UUID menuId,
            int quantity,
            List<OptionInfo> options
    ) {

    }

    private record OptionInfo(
            UUID optionId,
            List<UUID> optionValueIds
    ) {

    }
}

