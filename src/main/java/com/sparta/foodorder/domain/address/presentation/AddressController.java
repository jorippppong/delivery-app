package com.sparta.foodorder.domain.address.presentation;

import com.sparta.foodorder.domain.address.domain.AddressService;
import com.sparta.foodorder.domain.address.application.dto.AddressRequestDto;
import com.sparta.foodorder.domain.address.application.dto.AddressResponseDto;
import com.sparta.foodorder.domain.auth.infrastructure.util.SecurityUtil;
import com.sparta.foodorder.domain.user.domain.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배송지", description = "배송지 관련 API")
@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "배송지 등록", description = "새로운 배송지를 등록합니다")
    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(@Valid @RequestBody AddressRequestDto request) {
        User currentUser = SecurityUtil.getCurrentUser();
        AddressResponseDto response = addressService.createAddress(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "배송지 목록 조회", description = "사용자의 모든 배송지를 조회합니다")
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getUserAddresses() {
        User currentUser = SecurityUtil.getCurrentUser();
        List<AddressResponseDto> responses = addressService.getUserAddresses(currentUser.getId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "배송지 상세 조회", description = "배송지 상세 정보를 조회합니다")
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(@PathVariable Long addressId) {
        User currentUser = SecurityUtil.getCurrentUser();
        AddressResponseDto response = addressService.getAddress(addressId, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배송지 수정", description = "배송지 정보를 수정합니다")
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto request) {
        User currentUser = SecurityUtil.getCurrentUser();
        AddressResponseDto response = addressService.updateAddress(addressId, currentUser.getId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배송지 삭제", description = "배송지를 삭제합니다")
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        User currentUser = SecurityUtil.getCurrentUser();
        addressService.deleteAddress(addressId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기본 배송지 설정", description = "기본 배송지로 설정합니다")
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(@PathVariable Long addressId) {
        User currentUser = SecurityUtil.getCurrentUser();
        AddressResponseDto response = addressService.setDefaultAddress(addressId, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "기본 배송지 조회", description = "기본 배송지를 조회합니다")
    @GetMapping("/default")
    public ResponseEntity<AddressResponseDto> getDefaultAddress() {
        User currentUser = SecurityUtil.getCurrentUser();
        AddressResponseDto response = addressService.getDefaultAddress(currentUser.getId());
        return ResponseEntity.ok(response);
    }
}

