package com.sparta.foodorder.domain.store.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.store.application.dto.*;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "가게", description = "가게 관련 API")
@RestController
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @Operation(summary = "가게 생성", description = "새로운 가게를 등록합니다.")
    @PostMapping
    public ResponseEntity<StoreResponseDto> createStore(
        @Valid @RequestBody StoreCreateRequestDto storeCreateRequestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        UserRole role = userDetails.getRole();
        StoreResponseDto storeResponseDto = storeService.createStore(storeCreateRequestDto, userId, role);
        return ResponseEntity.ok(storeResponseDto);
    }

    @Operation(summary = "가게 수정", description = "가게 정보를 업데이트합니다.")
    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreResponseDto> updateStore(
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreUpdateRequestDto storeUpdateRequestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails.getUserEmail();
        UserRole role = userDetails.getRole();
        StoreResponseDto store = storeService.updateStore(storeUpdateRequestDto, storeId, email, role);
        return ResponseEntity.ok(store);
    }

    @Operation(summary = "가게 삭제", description = "가게를 삭제합니다.")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<Void> deleteStore(
        @PathVariable UUID storeId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails.getUserEmail();
        UserRole role = userDetails.getRole();
        storeService.deleteStore(storeId, email, role);
        return ResponseEntity.ok().build();
    }

    @Operation(
        summary = "가게목록 조회",
        description = "가게 목록을 조회합니다. 파라미터가 있으면 해당 키워드로 검색된 가게 목록을 조회합니다."
    )
    @GetMapping
    public ResponseEntity<PagedResponse<StoreResponseDto>> getStores(
        @RequestParam(name = "q", required = false) String query,
        Pageable pageable
    ) {
        PagedResponse<StoreResponseDto> stores = storeService.getStores(query, pageable);
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "가게 상세조회", description = "특정 가게를 상세조회합니다.")
    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailResponseDto> getStore(@PathVariable UUID storeId) {
        StoreDetailResponseDto store = storeService.getStore(storeId);
        return ResponseEntity.ok(store);
    }
}