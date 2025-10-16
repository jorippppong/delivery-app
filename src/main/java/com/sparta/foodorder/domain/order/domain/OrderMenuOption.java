package com.sparta.foodorder.domain.order.domain;

import com.sparta.foodorder.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "p_order_menu_option")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderMenuOption extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_menu_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private OrderMenu orderMenu;

    @OneToMany(mappedBy = "orderMenuOption", cascade = CascadeType.PERSIST)
    private List<OrderMenuOptionValue> values = new ArrayList<>();

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(name = "add_price", nullable = false)
    private int addPrice; // default = 0

    public OrderMenuOption(OrderMenu orderMenu, String optionName, int addPrice) {
        this.orderMenu = orderMenu;
        this.optionName = optionName;
        this.addPrice = addPrice;
    }

    public void setPrice(int optionTotalPrice) {
        this.addPrice = optionTotalPrice;
    }

    public void addValue(OrderMenuOptionValue value) {
        values.add(value);
        value.updateOrderMenuOption(this);
    }

    public void updateOrderMenu(OrderMenu orderMenu) {
        this.orderMenu = orderMenu;
    }
}
