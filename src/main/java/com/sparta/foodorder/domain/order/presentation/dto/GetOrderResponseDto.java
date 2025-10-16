package com.sparta.foodorder.domain.order.presentation.dto;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderMenu;
import com.sparta.foodorder.domain.order.domain.OrderMenuOptionValue;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GetOrderResponseDto(
        String customerNickname,
        String addressLine,
        String detailAddress,
        List<MenuInfo> menus,
        String status,
        int totalAmount,
        LocalDateTime createdAt
) {
    public static GetOrderResponseDto from(Order order, List<OrderMenu> orderMenus, String nickName) {
        List<MenuInfo> menus = orderMenus.stream()
                .map(menu -> new MenuInfo(
                        menu.getMenuId(),
                        menu.getMenuName(),
                        menu.getQuantity(),
                        menu.getOptions().stream()
                                .map(option -> new OptionInfo(
                                        option.getOptionName(),
                                        option.getValues().stream()
                                                .map(OrderMenuOptionValue::getOptionValueName)
                                                .toList()
                                ))
                                .toList()
                ))
                .toList();

        return new GetOrderResponseDto(
                nickName,
                order.getAddressLine(),
                order.getDetailAddress(),
                menus,
                order.getOrderStatus().name(),
                order.getTotalPrice(),
                order.getCreatedAt()
        );
    }

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

