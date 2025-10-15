package com.sparta.foodorder.domain.order.domain;

import com.sparta.foodorder.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_menu_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenuOption extends BaseCreateEntity {
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_menu_id")
    private Long orderMenuId; // 완전 종속

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "add_price", nullable = false)
    private int addPrice; // default = 0

    public OrderMenuOption(Long orderMenuId, String optionName, int addPrice) {
        this.orderMenuId = orderMenuId;
        this.optionName = optionName;
        this.addPrice = addPrice;
    }

    public void setPrice(int optionTotalPrice) {
        this.addPrice = optionTotalPrice;
    }
}
