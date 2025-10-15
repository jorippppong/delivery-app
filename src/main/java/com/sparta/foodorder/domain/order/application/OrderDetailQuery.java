package com.sparta.foodorder.domain.order.application;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderDetailQuery {
    private final UUID orderId;
    private final String orderStatus;
    private final int totalPrice;
    private final UUID storeId;
    private final Long userId;
    private String addressLine;
    private String detailAddress;
    private final LocalDateTime createdAt;
    private final List<OrderMenuQuery> menus;

    @QueryProjection
    public OrderDetailQuery(UUID orderId, String orderStatus, int totalPrice, UUID storeId, Long userId, String addressLine, String detailAddress,
                            LocalDateTime createdAt, List<OrderMenuQuery> menus) {
        this.orderId = orderId;
        this.orderStatus = orderStatus;
        this.totalPrice = totalPrice;
        this.storeId = storeId;
        this.userId = userId;
        this.addressLine = addressLine;
        this.detailAddress = detailAddress;
        this.createdAt = createdAt;
        this.menus = menus;
    }

    @Getter
    public static class OrderMenuQuery {
        private final UUID menuId;
        private final String menuName;
        private final int quantity;
        private final int price;
        private final List<OrderMenuOptionQuery> options;

        @QueryProjection
        public OrderMenuQuery(UUID menuId, String menuName, int quantity,
                              int price, List<OrderMenuOptionQuery> options) {
            this.menuId = menuId;
            this.menuName = menuName;
            this.quantity = quantity;
            this.price = price;
            this.options = options;
        }
    }

    @Getter
    public static class OrderMenuOptionQuery {
        private final String optionName;
        private final int addPrice;
        private final List<OrderMenuOptionValueQuery> values;

        @QueryProjection
        public OrderMenuOptionQuery(String optionName, int addPrice,
                                    List<OrderMenuOptionValueQuery> values) {
            this.optionName = optionName;
            this.addPrice = addPrice;
            this.values = values;
        }
    }

    @Getter
    public static class OrderMenuOptionValueQuery {
        private final String optionValueName;
        private final int addPrice;

        @QueryProjection
        public OrderMenuOptionValueQuery(String optionValueName, int addPrice) {
            this.optionValueName = optionValueName;
            this.addPrice = addPrice;
        }
    }
}

