package com.sparta.foodorder.domain.order.domain;

import lombok.Getter;

@Getter
public enum OrderStatus {
    CREATED("주문 생성"),
    PENDING("결제 완료, 주문 수락 대기 중"),
    CANCELED("고객 주문 취소"),
    ACCEPTED("사장 주문 수락"),
    REJECTED("사장 주문 거절"),
    READY("조리, 포장 완료"),
    DELIVERING("배달 중"),
    COMPLETE("고객 수령 완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public boolean isCancelable() {
        return this == CREATED || this == PENDING;
    }

    public boolean isAcceptable() {
        return this == PENDING;
    }

    public boolean isRejectable() {
        return this == PENDING;
    }

    public boolean isReadyable() {
        return this == ACCEPTED;
    }

    public boolean isDeliverable() {
        return this == READY;
    }

    public boolean isCompletable() {
        return this == DELIVERING;
    }
}
