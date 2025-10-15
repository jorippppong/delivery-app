package com.sparta.foodorder.domain.order.presentation.dto;

import com.sparta.foodorder.domain.order.application.OrderDetailQuery;
import com.sparta.foodorder.domain.order.domain.Order;

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
    public static GetOrderResponseDto from(Order order, OrderDetailQuery orderMenus, String nickName) {
        List<MenuInfo> menus = orderMenus.getMenus().stream()
                .map(menu -> new MenuInfo(
                        menu.getMenuId(),
                        menu.getMenuName(),
                        menu.getQuantity(),
                        menu.getOptions().stream()
                                .map(option -> new OptionInfo(
                                        option.getOptionName(),
                                        option.getValues().stream()
                                                .map(OrderDetailQuery.OrderMenuOptionValueQuery::getOptionValueName)
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
                orderMenus.getOrderStatus(),
                orderMenus.getTotalPrice(),
                orderMenus.getCreatedAt()
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

