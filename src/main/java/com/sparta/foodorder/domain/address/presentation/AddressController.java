package com.sparta.foodorder.domain.address.presentation;

import com.sparta.foodorder.domain.address.application.dto.AddressRequestDto;
import com.sparta.foodorder.domain.address.application.dto.AddressResponseDto;
import com.sparta.foodorder.domain.address.domain.AddressService;
import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배송지", description = "배송지 관련 API")
@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "배송지 등록", description = "현재 로그인한 사용자의 새로운 배송지를 등록합니다")
    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddressRequestDto request) {
        AddressResponseDto response = addressService.createAddress(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 배송지 목록 조회", description = "현재 로그인한 사용자의 모든 배송지를 조회합니다")
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getUserAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AddressResponseDto> responses = addressService.getUserAddresses(userDetails.getUserId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "배송지 상세 조회", description = "배송지 상세 정보를 조회합니다")
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        AddressResponseDto response = addressService.getAddress(addressId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배송지 수정", description = "배송지 정보를 수정합니다")
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto request) {
        AddressResponseDto response = addressService.updateAddress(addressId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배송지 삭제", description = "배송지를 삭제합니다")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        addressService.deleteAddress(addressId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기본 배송지 설정", description = "기본 배송지로 설정합니다")
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        AddressResponseDto response = addressService.setDefaultAddress(addressId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "기본 배송지 조회", description = "현재 로그인한 사용자의 기본 배송지를 조회합니다")
    @GetMapping("/default")
    public ResponseEntity<AddressResponseDto> getDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AddressResponseDto response = addressService.getDefaultAddress(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
