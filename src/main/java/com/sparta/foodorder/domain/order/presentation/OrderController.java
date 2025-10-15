package com.sparta.foodorder.domain.order.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.order.application.OrderService;
import com.sparta.foodorder.domain.order.presentation.dto.*;
import com.sparta.foodorder.global.dto.PagedResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CreateOrderResponseDto> createOrder(
            @RequestBody @Valid CreateOrderRequestDto dto,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        UUID orderId = orderService.createOrder(dto, userId);
        return ResponseEntity.ok(new CreateOrderResponseDto(orderId));
    }

    @PostMapping("/orders/{orderId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> cancelOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        orderService.cancelOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/accept")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> acceptOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        orderService.acceptOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/reject")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> rejectOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        orderService.rejectOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/ready")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> readyOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        orderService.readyOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/deliver")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<Void> deliverOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        orderService.deliverOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/orders/{orderId}/complete")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> completeOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        orderService.completeOrder(orderId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/users/orders")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<PagedResponse<GetUserOrdersResponseDto>> getUserOrders(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        PagedResponse<GetUserOrdersResponseDto> response = orderService.getUserOrders(userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/stores/{storeId}/orders")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<PagedResponse<GetStoreOrdersResponseDto>> getStoreOrders(
            @PathVariable("storeId") UUID storeId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        PagedResponse<GetStoreOrdersResponseDto> response = orderService.getStoreOrders(storeId, userId, page, size);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<GetOrderResponseDto> getOrder(
            @PathVariable("orderId") UUID orderId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        String nickname = userDetails.getNickName();
        GetOrderResponseDto response = orderService.getOrder(orderId, userId, nickname);
        return ResponseEntity.ok(response);
    }
}
