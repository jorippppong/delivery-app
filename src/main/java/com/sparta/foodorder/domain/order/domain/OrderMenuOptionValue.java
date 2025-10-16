package com.sparta.foodorder.domain.order.domain;

import com.sparta.foodorder.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_order_menu_option_value")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderMenuOptionValue extends BaseCreateEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_menu_option_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OrderMenuOption orderMenuOption;

    @Column(name = "option_value_name", nullable = false)
    private String optionValueName;

    @Column(name = "add_price", nullable = false)
    private int addPrice; // default = 0

    public OrderMenuOptionValue(OrderMenuOption orderMenuOption, String optionValueName, int addPrice) {
        this.orderMenuOption = orderMenuOption;
        this.optionValueName = optionValueName;
        this.addPrice = addPrice;
    }

    public void updateOrderMenuOption(OrderMenuOption orderMenuOption) {
        this.orderMenuOption = orderMenuOption;
    }
}
