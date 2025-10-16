package com.sparta.foodorder.domain.order.domain;

import com.sparta.foodorder.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_order_menu")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class OrderMenu extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false, foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Order order;

    @OneToMany(mappedBy = "orderMenu", cascade = CascadeType.PERSIST)
    private List<OrderMenuOption> options = new ArrayList<>();

    @Column(name = "menu_id")
    private UUID menuId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "menu_name", nullable = false)
    private String menuName;

    @Column(name = "total_price", nullable = false)
    private int totalPrice;

    public OrderMenu(Order order, UUID menuId, int quantity, String menuName, int totalPrice) {
        this.order = order;
        this.menuId = menuId;
        this.quantity = quantity;
        this.menuName = menuName;
        this.totalPrice = totalPrice;
    }

    public void updateTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }

    public void addOption(OrderMenuOption option) {
        options.add(option);
        option.updateOrderMenu(this);
    }
}
