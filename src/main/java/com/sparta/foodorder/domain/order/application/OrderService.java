package com.sparta.foodorder.domain.order.application;

import com.sparta.foodorder.domain.menu.application.MenuService;
import com.sparta.foodorder.domain.menu.application.OptionService;
import com.sparta.foodorder.domain.menu.application.OptionValueService;
import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.menu.domain.Option;
import com.sparta.foodorder.domain.menu.domain.OptionValue;
import com.sparta.foodorder.domain.order.domain.*;
import com.sparta.foodorder.domain.order.presentation.dto.CreateOrderRequestDto;
import com.sparta.foodorder.domain.order.presentation.dto.GetOrderResponseDto;
import com.sparta.foodorder.domain.order.presentation.dto.GetStoreOrdersResponseDto;
import com.sparta.foodorder.domain.order.presentation.dto.GetUserOrdersResponseDto;
import com.sparta.foodorder.domain.payment.domain.Payment;
import com.sparta.foodorder.domain.payment.domain.PaymentService;
import com.sparta.foodorder.domain.payment.event.PaymentEvent;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserService;
import com.sparta.foodorder.global.dto.PagedResponse;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderMenuRepository orderMenuRepository;
    private final PaymentService paymentService;
    private final StoreService storeService;
    private final MenuService menuService;
    private final UserService userService;
    private final OptionService optionService;
    private final OptionValueService optionValueService;
    private final ApplicationEventPublisher applicationEventPublisher;

    private Order getOrder(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    public UUID createOrder(CreateOrderRequestDto dto, Long userId) {
        storeService.validateExistenceById(dto.storeId());
        List<Menu> menus = menuService.findAllByIds(dto.getMenuIds());
        Map<UUID, Menu> menuMap = menus.stream()
                .collect(Collectors.toMap(Menu::getId, m -> m));

        // order 생성
        Order order = new Order(userId, dto.storeId(), dto.addressLine(), dto.detailAddress(), 0, dto.memo());
        UUID orderId = orderRepository.save(order);

        // orderMenu 생성
        List<OrderMenu> orderMenus = new ArrayList<>();
        List<OrderMenuOption> orderMenuOptions = new ArrayList<>();
        List<OrderMenuOptionValue> orderMenuOptionValues = new ArrayList<>();
        int totalPrice = 0;

        for (CreateOrderRequestDto.MenuInfo menuInfo : dto.menus()) {
            Menu menu = menuMap.get(menuInfo.menuId());

            int menuTotalPrice = menu.getPrice() * menuInfo.quantity();
            OrderMenu orderMenu = new OrderMenu(orderId, menu.getId(), menuInfo.quantity(), menu.getName(), 0);
            orderMenus.add(orderMenu);

            // 옵션 처리
            for (CreateOrderRequestDto.OptionInfo optionInfo : menuInfo.options()) {
                Option option = optionService.findById(optionInfo.optionId());
                OrderMenuOption orderMenuOption = new OrderMenuOption(orderMenu.getId(), option.getName(), 0);

                int optionTotalPrice = 0;
                for (UUID valueId : optionInfo.optionValueIds()) {
                    OptionValue value = optionValueService.findById(valueId);
                    OrderMenuOptionValue orderMenuOptionValue = new OrderMenuOptionValue(
                            orderMenuOption.getId(),
                            value.getValue(),
                            value.getAddPrice()
                    );
                    orderMenuOptionValues.add(orderMenuOptionValue);
                    optionTotalPrice += value.getAddPrice();
                }

                orderMenuOption.setPrice(optionTotalPrice);
                orderMenuOptions.add(orderMenuOption);
                menuTotalPrice += optionTotalPrice;
            }

            orderMenu.updateTotalPrice(menuTotalPrice);
            totalPrice += menuTotalPrice;
        }

        orderMenuRepository.saveAllOrderMenu(orderMenus);
        orderMenuRepository.saveAllOrderMenuOption(orderMenuOptions);
        orderMenuRepository.saveAllOrderMenuOptionValue(orderMenuOptionValues);

        order.updateTotalPrice(totalPrice);

        return orderId;
    }

    public void cancelOrder(UUID orderId, Long userId) {
        Order order = getOrder(orderId);
        order.validateOrderWriter(userId);
        order.cancel();

        // 이벤트 전송 (orderId 받으면 환불 처리)
        Optional<Payment> optionalPayment = paymentService.findByOrderId(orderId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            PaymentEvent.PaymentRefunded refunded = new PaymentEvent.PaymentRefunded(
                    orderId,
                    payment.getId(),
                    "사용자 주문 취소"
            );
            applicationEventPublisher.publishEvent(refunded);
            log.info("주문 취소 이벤트 발행 - orderId : {}", orderId);
        }
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

    public PagedResponse<GetUserOrdersResponseDto> getUserOrders(Long userId, int page, int size) {
        List<OrderDetailQuery> orders = orderRepository.getUserOrders(userId, page - 1, size);
        Set<UUID> storeIds = orders.stream()
                .map(OrderDetailQuery::getStoreId)
                .collect(Collectors.toSet());
        List<Store> stores = storeService.findAllByIds(storeIds);
        Map<UUID, Store> storeMap = stores.stream()
                .collect(Collectors.toMap(Store::getId, s -> s));

        List<GetUserOrdersResponseDto> dtos = orders.stream()
                .map(order -> {
                    Store store = storeMap.get(order.getStoreId());
                    return GetUserOrdersResponseDto.from(order, store);
                })
                .toList();
        boolean hasNext = dtos.size() > size;
        List<GetUserOrdersResponseDto> pageContent = hasNext ? dtos.subList(0, size) : dtos;
        return PagedResponse.success(pageContent, page, pageContent.size(), hasNext);
    }

    public PagedResponse<GetStoreOrdersResponseDto> getStoreOrders(UUID storeId, Long userId, int page, int size) {
        Store store = storeService.findByUUID(storeId);
        store.validateOwner(userId);
        List<OrderDetailQuery> orders = orderRepository.getStoreOrders(storeId, page - 1, size);
        Set<Long> userIds = orders.stream()
                .map(OrderDetailQuery::getUserId)
                .collect(Collectors.toSet());
        List<User> users = userService.findAllByIdIn(userIds);
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        List<GetStoreOrdersResponseDto> dtos = orders.stream()
                .map(order -> {
                    User user = userMap.get(order.getUserId());
                    return GetStoreOrdersResponseDto.from(order, user);
                })
                .toList();
        boolean hasNext = dtos.size() > size;
        List<GetStoreOrdersResponseDto> pageContent = hasNext ? dtos.subList(0, size) : dtos;
        return PagedResponse.success(pageContent, page, pageContent.size(), hasNext);
    }

    public GetOrderResponseDto getOrder(UUID orderId, Long userId, String nickname) {
        Order order = getOrder(orderId);
        order.validateOrderWriter(userId);
        OrderDetailQuery orderDetail = orderRepository.getOrderDetail(orderId);
        return GetOrderResponseDto.from(order, orderDetail, nickname);
    }
}
