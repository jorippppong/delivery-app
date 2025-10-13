package com.sparta.foodorder.domain.order.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "p_order_menu_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderMenuOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // optionId, optionValueId id 고민
    @Column(name = "order_menu_id")
    private UUID orderMenuId;

    @Column(name = "option_id")
    private UUID optionId;

    @Column(name = "option_value_id")
    private UUID optionValueId;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "option_value", nullable = false)
    private String optionValue;

    @Column(name = "add_price", nullable = false)
    private int addPrice; // default = 0
}
