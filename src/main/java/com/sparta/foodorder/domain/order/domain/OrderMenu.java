package com.sparta.foodorder.domain.order.domain;

import com.sparta.foodorder.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_order_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenu extends BaseCreateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false)
    private UUID orderId;

    @Column(name = "menu_id")
    private UUID menuId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    public OrderMenu(UUID orderId, UUID menuId, int quantity, String menuName, int totalPrice) {

    }
}
