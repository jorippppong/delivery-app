package com.sparta.foodorder.domain.order.domain;

import com.sparta.foodorder.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_menu_option_value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenuOptionValue extends BaseCreateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_menu_option_id")
    private Long orderMenuOptionId; // 완전 종속

    @Column(name = "option_value_name", nullable = false)
    private String optionValueName;

    @Column(name = "add_price", nullable = false)
    private int addPrice; // default = 0

    public OrderMenuOptionValue(Long orderMenuOptionId, String optionValueName, int addPrice) {
        this.orderMenuOptionId = orderMenuOptionId;
        this.optionValueName = optionValueName;
        this.addPrice = addPrice;
    }
}
