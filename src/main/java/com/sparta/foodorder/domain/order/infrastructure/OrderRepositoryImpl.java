package com.sparta.foodorder.domain.order.infrastructure;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.sparta.foodorder.domain.order.application.*;
import com.sparta.foodorder.domain.order.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static com.querydsl.core.group.GroupBy.groupBy;
import static com.querydsl.core.group.GroupBy.list;

@Repository
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;

    private final JPAQueryFactory queryFactory;
    private QOrder order = QOrder.order;
    private QOrderMenu orderMenu = QOrderMenu.orderMenu;
    private QOrderMenuOption orderMenuOption = QOrderMenuOption.orderMenuOption;
    private QOrderMenuOptionValue orderMenuOptionValue = QOrderMenuOptionValue.orderMenuOptionValue;


    @Override
    public Optional<Order> findById(UUID orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public UUID save(Order order) {
        return orderJpaRepository.save(order).getId();
    }

    @Override
    public OrderDetailQuery getOrderDetail(UUID orderId) {
        Map<UUID, OrderDetailQuery> result = queryFactory
                .from(order)
                .join(orderMenu).on(orderMenu.orderId.eq(order.id))
                .leftJoin(orderMenuOption).on(orderMenuOption.orderMenuId.eq(orderMenu.id))
                .leftJoin(orderMenuOptionValue).on(orderMenuOptionValue.orderMenuOptionId.eq(orderMenuOption.id))
                .where(order.id.eq(orderId))
                .transform(groupBy(order.id).as(
                        new QOrderDetailQuery(
                                order.id,
                                order.orderStatus.stringValue(),
                                order.totalPrice,
                                order.storeId,
                                order.userId,
                                order.addressLine,
                                order.detailAddress,
                                order.createdAt,
                                list(new QOrderDetailQuery_OrderMenuQuery(
                                        orderMenu.menuId,
                                        orderMenu.menuName,
                                        orderMenu.quantity,
                                        orderMenu.totalPrice,
                                        list(new QOrderDetailQuery_OrderMenuOptionQuery(
                                                orderMenuOption.optionName,
                                                orderMenuOption.addPrice,
                                                list(new QOrderDetailQuery_OrderMenuOptionValueQuery(
                                                        orderMenuOptionValue.optionValueName,
                                                        orderMenuOptionValue.addPrice
                                                ))
                                        ))
                                ))
                        )
                ));
        return result.get(orderId);
    }

    @Override
    public List<OrderDetailQuery> getUserOrders(Long userId, int page, int size) {
        int offset = page * size;

        Map<UUID, OrderDetailQuery> result = queryFactory
                .from(order)
                .join(orderMenu).on(orderMenu.orderId.eq(order.id))
                .leftJoin(orderMenuOption).on(orderMenuOption.orderMenuId.eq(orderMenu.id))
                .leftJoin(orderMenuOptionValue).on(orderMenuOptionValue.orderMenuOptionId.eq(orderMenuOption.id))
                .where(order.userId.eq(userId))
                .orderBy(order.createdAt.desc())
                .offset(offset)
                .limit(size + 1)
                .transform(groupBy(order.id).as(
                        new QOrderDetailQuery(
                                order.id,
                                order.orderStatus.stringValue(),
                                order.totalPrice,
                                order.storeId,
                                order.userId,
                                order.addressLine,
                                order.detailAddress,
                                order.createdAt,
                                list(new QOrderDetailQuery_OrderMenuQuery(
                                        orderMenu.menuId,
                                        orderMenu.menuName,
                                        orderMenu.quantity,
                                        orderMenu.totalPrice,
                                        list(new QOrderDetailQuery_OrderMenuOptionQuery(
                                                orderMenuOption.optionName,
                                                orderMenuOption.addPrice,
                                                list(new QOrderDetailQuery_OrderMenuOptionValueQuery(
                                                        orderMenuOptionValue.optionValueName,
                                                        orderMenuOptionValue.addPrice
                                                ))
                                        ))
                                ))
                        )
                ));

        return result.values().stream().toList();
    }

    @Override
    public List<OrderDetailQuery> getStoreOrders(UUID storeId, int page, int size) {
        int offset = page * size;

        Map<UUID, OrderDetailQuery> result = queryFactory
                .from(order)
                .join(orderMenu).on(orderMenu.orderId.eq(order.id))
                .leftJoin(orderMenuOption).on(orderMenuOption.orderMenuId.eq(orderMenu.id))
                .leftJoin(orderMenuOptionValue).on(orderMenuOptionValue.orderMenuOptionId.eq(orderMenuOption.id))
                .where(order.storeId.eq(storeId))
                .orderBy(order.createdAt.desc())
                .offset(offset)
                .limit(size + 1)
                .transform(groupBy(order.id).as(
                        new QOrderDetailQuery(
                                order.id,
                                order.orderStatus.stringValue(),
                                order.totalPrice,
                                order.storeId,
                                order.userId,
                                order.addressLine,
                                order.detailAddress,
                                order.createdAt,
                                list(new QOrderDetailQuery_OrderMenuQuery(
                                        orderMenu.menuId,
                                        orderMenu.menuName,
                                        orderMenu.quantity,
                                        orderMenu.totalPrice,
                                        list(new QOrderDetailQuery_OrderMenuOptionQuery(
                                                orderMenuOption.optionName,
                                                orderMenuOption.addPrice,
                                                list(new QOrderDetailQuery_OrderMenuOptionValueQuery(
                                                        orderMenuOptionValue.optionValueName,
                                                        orderMenuOptionValue.addPrice
                                                ))
                                        ))
                                ))
                        )
                ));

        return result.values().stream().toList();
    }
}
