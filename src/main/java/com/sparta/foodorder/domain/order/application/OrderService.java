package com.sparta.foodorder.domain.order.application;

import com.sparta.foodorder.domain.menu.application.MenuService;
import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.order.domain.*;
import com.sparta.foodorder.domain.order.presentation.dto.CreateOrderRequestDto;
import com.sparta.foodorder.domain.order.presentation.dto.GetOrderResponseDto;
import com.sparta.foodorder.domain.order.presentation.dto.GetStoreOrdersResponseDto;
import com.sparta.foodorder.domain.order.presentation.dto.GetUserOrdersResponseDto;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final StoreService storeService;
    private final MenuService menuService;

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    // option, option value는 상황 보고 추가
    public UUID createOrder(CreateOrderRequestDto dto, Long userId) {
        storeService.validateExistenceById(dto.storeId());
        // 메뉴 옵션 존재하는지 확인
        // 메뉴 옵션 value 존재하는지 확인

        List<Menu> menus = menuService.findAllByIds(dto.getMenuIds());
        Map<UUID, Menu> menuMap = menus.stream()
                .collect(Collectors.toMap(Menu::getId, m -> m));

        int totalPrice = calculateTotalPrice(dto.getMenuIdAndQuantity(), menuMap);

        Order order = new Order(userId, dto.storeId(), dto.addressLine(), dto.detailAddress(), totalPrice, dto.memo());
        UUID orderId = orderRepository.save(order);

        List<OrderMenu> orderMenus = new ArrayList<>();
        for (CreateOrderRequestDto.MenuInfo menuInfo : dto.menus()) {
            Menu menu = menuMap.get(menuInfo.menuId());
            OrderMenu orderMenu = new OrderMenu(orderId, menu.getId(), menuInfo.quantity(), menu.getName(), menuInfo.quantity() * menu.getPrice());
            orderMenus.add(orderMenu);
        }
        orderMenuRepository.saveAll(orderMenus);
        // 주문 value 연관관계 추가

        return orderId;
    }

    private int calculateTotalPrice(Map<UUID, Integer> menuMap, Map<UUID, Menu> menuEntityMap) {
        int totalPrice = 0;
        for (Map.Entry<UUID, Integer> entry : menuMap.entrySet()) {
            UUID menuId = entry.getKey();
            int quantity = entry.getValue();
            Menu menu = menuEntityMap.get(menuId);
            if (menu == null) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
            }
            totalPrice += quantity * menu.getPrice();
        }
        return totalPrice;
    }

    // 결제 끝나고 주문 상태 변경 (created -> pending) : 이벤트 발행하여 처리할 예정
    // TODO : 결제 이벤트 발행
    public void cancelOrder(UUID orderId, Long userId) {
        Order order = getOrder(orderId);
        order.validateOrderWriter(userId);
        order.cancel();
    }

    public void acceptOrder(UUID orderId, Long userId) {
        Order order = getOrder(orderId);
        Store store = storeService.findByUUID(order.getStoreId());
        store.validateOwner(userId);
        order.accept();
    }

    public void rejectOrder(UUID orderId, Long userId) {
        Order order = getOrder(orderId);
        Store store = storeService.findByUUID(order.getStoreId());
        store.validateOwner(userId);
        order.reject();
    }

    public void readyOrder(UUID orderId, Long userId) {
        Order order = getOrder(orderId);
        Store store = storeService.findByUUID(order.getStoreId());
        store.validateOwner(userId);
        order.ready();
    }

    public void deliverOrder(UUID orderId, Long userId) {
        Order order = getOrder(orderId);
        Store store = storeService.findByUUID(order.getStoreId());
        store.validateOwner(userId);
        order.deliver();
    }

    public void completeOrder(UUID orderId, Long userId) {
        Order order = getOrder(orderId);
        order.validateOrderWriter(userId);
        order.complete();
    }

    public List<GetUserOrdersResponseDto> getUserOrders(Long userId, int page, int size) {
        return null;

    }

    public List<GetStoreOrdersResponseDto> getStoreOrders(UUID storeId, Long userId, int page, int size, OrderStatus status) {
        return null;
    }

    public GetOrderResponseDto getOrder(UUID orderId, Long userId) {
        return null;
    }
}
