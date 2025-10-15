package com.sparta.foodorder.domain.order.presentation.dto;

import com.sparta.foodorder.domain.order.application.OrderDetailQuery;
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
    public static GetUserOrdersResponseDto from(OrderDetailQuery orderDetail, Store store) {
        List<MenuInfo> menus = orderDetail.getMenus().stream()
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

        return new GetUserOrdersResponseDto(
                orderDetail.getOrderId(),
                store.getId(),
                store.getName(),
                menus,
                orderDetail.getOrderStatus(),
                orderDetail.getTotalPrice(),
                orderDetail.getCreatedAt()
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
