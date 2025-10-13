package com.sparta.foodorder.domain.order.application;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderRepository;
import com.sparta.foodorder.domain.order.domain.OrderStatus;
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

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;
    private final StoreService storeService;

    // 주문 생성 (created)
    public UUID createOrder(CreateOrderRequestDto dto, Long userId) {
        return null;
    }

    // 결제 끝나고 주문 상태 변경 (created -> pending) : 이벤트 발행하여 처리할 예정

    // TODO : 결제 이벤트 발행
    public void cancelOrder(UUID orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.validateOrderWriter(userId);
        order.cancel();
    }

    public void acceptOrder(UUID orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        Store store = storeService.findById(order.getStoreId());
        store.validateOwner(userId);
        order.accept();
    }

    // (사장) pending -> reject 
    public void rejectOrder(UUID orderId, Long userId) {

    }

    // (사장) accept -> ready
    public void readyOrder(UUID orderId, Long userId) {

    }

    // (사장) ready -> delivering


    // (고객) delivering -> complete
    public void completeOrder(UUID orderId, Long userId) {

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
