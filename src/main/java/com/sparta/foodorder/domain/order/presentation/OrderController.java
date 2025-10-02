package com.sparta.foodorder.domain.order.presentation;

import com.sparta.foodorder.domain.order.application.OrderService;
import com.sparta.foodorder.domain.order.domain.OrderStatus;
import com.sparta.foodorder.domain.order.presentation.dto.*;
import com.sparta.foodorder.global.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<CreateOrderResponseDto> createOrder(
            @RequestBody @Valid CreateOrderRequestDto dto
    ) {
        Long userId = 1L; // (추후 변경)
        UUID orderId = orderService.createOrder(dto, userId);
        return ResponseEntity.ok(new CreateOrderResponseDto(orderId));
    }

    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable("orderId") UUID orderId
    ) {
        Long userId = 1L; // (추후 변경)
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/accept")
    public ResponseEntity<Void> acceptOrder(
            @PathVariable("orderId") UUID orderId
    ) {
        Long userId = 1L; // (추후 변경)
        orderService.acceptOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/reject")
    public ResponseEntity<Void> rejectOrder(
            @PathVariable("orderId") UUID orderId
    ) {
        Long userId = 1L; // (추후 변경)
        orderService.rejectOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/ready")
    public ResponseEntity<Void> readyOrder(
            @PathVariable("orderId") UUID orderId
    ) {
        Long userId = 1L; // (추후 변경)
        orderService.readyOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/complete")
    public ResponseEntity<Void> completeOrder(
            @PathVariable("orderId") UUID orderId
    ) {
        Long userId = 1L; // (추후 변경)
        orderService.completeOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/orders")
    public ResponseEntity<PagedResponse<GetUserOrdersResponseDto>> getUserOrders(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "15") int size
    ) {
        Long userId = 1L; // (추후 변경)
        List<GetUserOrdersResponseDto> response = orderService.getUserOrders(userId, page, size);
        return null;
    }

    @GetMapping("/stores/{storeId}/orders")
    public ResponseEntity<PagedResponse<GetStoreOrdersResponseDto>> getStoreOrders(
            @PathVariable("storeId") UUID storeId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "15") int size,
            @RequestParam(value = "status") OrderStatus status
    ) {
        Long userId = 1L; // (추후 변경)
        List<GetStoreOrdersResponseDto> response = orderService.getStoreOrders(storeId, userId, page, size, status);
        return null;
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<GetOrderResponseDto> getOrder(
            @PathVariable("orderId") UUID orderId
    ) {
        Long userId = 1L; // (추후 변경)
        GetOrderResponseDto response = orderService.getOrder(orderId, userId);
        return null;
    }
}
