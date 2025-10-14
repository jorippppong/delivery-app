package com.sparta.foodorder.domain.store.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.store.application.dto.CategoryCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryResponseDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryUpdateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.StoreResponseDto;
import com.sparta.foodorder.domain.store.domain.CategoryService;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.domain.user.domain.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "카테고리", description = "카테고리 관련 API")
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @Operation(summary = "카테고리별 가게 조회", description = "카테고리별 가게를 조회합니다.")
    @GetMapping("/{categoryId}/stores")
    public ResponseEntity<List<StoreResponseDto>> getStoresByCategory(
        @PathVariable UUID categoryId
    ) {
        List<StoreResponseDto> stores = storeService.getStoresByCategory(categoryId);
        return ResponseEntity.ok(stores);
    }

    @Operation(summary = "카테고리 생성", description = "관리자는 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
        @Valid @RequestBody CategoryCreateRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        UserRole role = userDetails.getUser().getRole();
        CategoryResponseDto response = categoryService.createCategory(requestDto, userId, role);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "카테고리 수정", description = "관리자는 카테고리명을 수정합니다.")
    @PatchMapping("{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
        @PathVariable UUID categoryId,
        @Valid @RequestBody CategoryUpdateRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        UserRole role = userDetails.getUser().getRole();
        CategoryResponseDto response =
            categoryService.updateCategory(categoryId, requestDto, userId, role);
        return ResponseEntity.ok(response);
    }
}
