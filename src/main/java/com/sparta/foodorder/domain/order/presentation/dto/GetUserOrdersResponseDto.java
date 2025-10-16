package com.sparta.foodorder.domain.order.presentation.dto;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderMenu;
import com.sparta.foodorder.domain.order.domain.OrderMenuOptionValue;
import com.sparta.foodorder.domain.store.domain.Store;

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
    public static GetUserOrdersResponseDto from(Order order, List<OrderMenu> orderMenus, Store store) {
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

        return new GetUserOrdersResponseDto(
                order.getId(),
                store.getId(),
                store.getName(),
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
