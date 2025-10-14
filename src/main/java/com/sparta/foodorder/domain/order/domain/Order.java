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

    @Getter
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

    @Column(name = "address_line", nullable = false)
    private String addressLine;

    @Column(name = "detail_address", nullable = false)
    private String detailAddress;

    @Column(name = "memo", length = 200)
    private String memo;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Getter
    @Column(name = "store_id", nullable = false)
    private UUID storeId;

    public Order(Long userId, UUID storeId, String addressLine, String detailAddress, int totalPrice, String memo) {
        validateMemo(memo);

        this.orderStatus = OrderStatus.CREATED;
        this.orderedAt = LocalDateTime.now();
        this.totalPrice = totalPrice;
        this.addressLine = addressLine;
        this.detailAddress = detailAddress;
        this.memo = memo.trim();
        this.userId = userId;
        this.storeId = storeId;
    }

    public void validateOrderWriter(Long userId) {
        if (!this.userId.equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_CANT_ACCESS);
        }
    }

    // created, pending  -> canceled
    public void cancel() {
        if (orderStatus.equals(OrderStatus.CREATED)) {
            this.orderStatus = OrderStatus.CANCELED;
        } else if (orderStatus.equals(OrderStatus.PENDING)) {
            // TODO : 이벤트 전송
            this.orderStatus = OrderStatus.CANCELED;
        }
        throw new BusinessException(ErrorCode.ORDER_CANCEL_NOT_ALLOWED);
    }

    // pending -> accept
    public void accept() {
        if (!orderStatus.isAcceptable()) {
            throw new BusinessException(ErrorCode.ORDER_ACCEPT_NOT_ALLOWED);
        }
        this.orderStatus = OrderStatus.ACCEPTED;
    }

    // pending -> reject
    public void reject() {
        if (!orderStatus.isRejectable()) {
            throw new BusinessException(ErrorCode.ORDER_REJECT_NOT_ALLOWED);
        }
        this.orderStatus = OrderStatus.REJECTED;
    }

    // accept -> ready
    public void ready() {
        if (!orderStatus.isReadyable()) {
            throw new BusinessException(ErrorCode.ORDER_READY_NOT_ALLOWED);
        }
        this.orderStatus = OrderStatus.READY;
    }

    // ready -> delivering
    public void deliver() {
        if (!orderStatus.isDeliverable()) {
            throw new BusinessException(ErrorCode.ORDER_DELIVER_NOT_ALLOWED);
        }
        this.orderStatus = OrderStatus.DELIVERING;
    }

    // delivering -> complete
    public void complete() {
        if (!orderStatus.isCompletable()) {
            throw new BusinessException(ErrorCode.ORDER_COMPLETE_NOT_ALLOWED);
        }
        this.orderStatus = OrderStatus.COMPLETE;
    }

    private void validateMemo(String memo) {
        if (memo != null && memo.trim().length() > 200) {
            throw new BusinessException(ErrorCode.ORDER_MEMO_LENGTH);
        }
    }
}
