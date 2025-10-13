package com.sparta.foodorder.domain.order.domain;

import com.sparta.foodorder.global.common.BaseUpdateEntity;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "p_order")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order extends BaseUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    @Column(name = "delivery_address", nullable = false)
    private String deliveryAddress;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    public UUID getStoreId() {
        return storeId;
    }

    public void validateOrderWriter(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_CANT_ACCESS);
        }
    }

    public void cancel() {
        // created, pending  -> canceled
        if (orderStatus.equals(OrderStatus.CREATED)) {
            this.orderStatus = OrderStatus.CANCELED;
        } else if (orderStatus.equals(OrderStatus.PENDING)) {
            // TODO : 이벤트 전송
            this.orderStatus = OrderStatus.CANCELED;
        }
        throw new BusinessException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
    }

    public void accept() {
        // pending -> accept
        if (orderStatus.isAcceptable()) {
            this.orderStatus = OrderStatus.ACCEPTED;
        }
    }
}
