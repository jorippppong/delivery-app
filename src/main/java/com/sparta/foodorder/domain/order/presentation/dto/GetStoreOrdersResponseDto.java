package com.sparta.foodorder.domain.order.presentation.dto;

import com.sparta.foodorder.domain.order.application.OrderDetailQuery;
import com.sparta.foodorder.domain.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GetStoreOrdersResponseDto(
        UUID orderId,
        String customerNickname,
        String addressLine,
        String detailAddress,
        List<MenuInfo> menus,
        String status,
        int totalAmount,
        LocalDateTime createdAt
) {

    public static GetStoreOrdersResponseDto from(OrderDetailQuery orderDetail, User user) {
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

        return new GetStoreOrdersResponseDto(
                orderDetail.getOrderId(),
                user.getNickName(),
                orderDetail.getAddressLine(),
                orderDetail.getDetailAddress(),
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
