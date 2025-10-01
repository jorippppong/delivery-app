package com.sparta.foodorder.domain.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_menu_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenuOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // optionId, optionValueId id 고민
    @Column(name = "order_menu_id")
    private Long orderMenuId;

    @Column(name = "option_id")
    private Long optionId;

    @Column(name = "option_value_id")
    private Long optionValueId;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "option_value", nullable = false)
    private String optionValue;

    @Column(name = "add_price", nullable = false)
    private int addPrice; // default = 0
}
