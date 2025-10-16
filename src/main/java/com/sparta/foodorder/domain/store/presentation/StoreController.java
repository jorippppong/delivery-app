package com.sparta.foodorder.domain.store.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.store.application.dto.*;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.dto.PagedResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/stores")
@RequiredArgsConstructor
public class StoreController implements StoreApiDocs {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<StoreResponseDto> createStore(
        @Valid @RequestBody StoreCreateRequestDto storeCreateRequestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        UserRole role = userDetails.getRole();
        StoreResponseDto storeResponseDto = storeService.createStore(storeCreateRequestDto, userId,
            role);
        return ResponseEntity.ok(storeResponseDto);
    }

    @PatchMapping("/{storeId}")
    public ResponseEntity<StoreResponseDto> updateStore(
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreUpdateRequestDto storeUpdateRequestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        String email = userDetails.getUserEmail();
        UserRole role = userDetails.getRole();
        StoreResponseDto store = storeService.updateStore(storeUpdateRequestDto, storeId, email,
            role);
        return ResponseEntity.ok(store);
    }

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

    @GetMapping
    public ResponseEntity<PagedResponse<StoreResponseDto>> getStores(
        @RequestParam(name = "q", required = false) String query,
        @PageableDefault(size = 10)
        @SortDefault(sort = "createdAt", direction = Direction.DESC)
        Pageable pageable,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserRole role = userDetails.getRole();
        PagedResponse<StoreResponseDto> stores = storeService.getStores(query, pageable, role);
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{storeId}")
    public ResponseEntity<StoreDetailResponseDto> getStore(
        @PathVariable UUID storeId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserRole role = userDetails.getRole();
        StoreDetailResponseDto store = storeService.getStore(storeId, role);
        return ResponseEntity.ok(store);
    }
}