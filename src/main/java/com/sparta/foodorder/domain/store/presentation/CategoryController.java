package com.sparta.foodorder.domain.store.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.store.application.dto.CategoryCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryResponseDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryUpdateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.StoreResponseDto;
import com.sparta.foodorder.domain.store.domain.CategoryService;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController implements CategoryApiDocs {

    private final StoreService storeService;
    private final CategoryService categoryService;

    @GetMapping("/{categoryId}/stores")
    public ResponseEntity<PagedResponse<StoreResponseDto>> getStoresByCategory(
        @PathVariable UUID categoryId,
        @PageableDefault(size = 10)
        @SortDefault(sort = "createdAt", direction = Direction.DESC)
        Pageable pageable,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        UserRole role = userDetails.getRole();
        PagedResponse<StoreResponseDto> stores = storeService.getStoresByCategory(categoryId,
            pageable, role);
        return ResponseEntity.ok(stores);
    }

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
        @Valid @RequestBody CategoryCreateRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        UserRole role = userDetails.getRole();
        CategoryResponseDto response = categoryService.createCategory(requestDto, userId, role);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("{categoryId}")
    public ResponseEntity<CategoryResponseDto> updateCategory(
        @PathVariable UUID categoryId,
        @Valid @RequestBody CategoryUpdateRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        Long userId = userDetails.getUserId();
        UserRole role = userDetails.getRole();
        CategoryResponseDto response =
            categoryService.updateCategory(categoryId, requestDto, userId, role);
        return ResponseEntity.ok(response);
    }
}
