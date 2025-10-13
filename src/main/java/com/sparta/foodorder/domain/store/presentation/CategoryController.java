package com.sparta.foodorder.domain.store.presentation;

import com.sparta.foodorder.domain.store.application.dto.StoreResponseDto;
import com.sparta.foodorder.domain.store.domain.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "카테고리", description = "카테고리 관련 API")
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final StoreService storeService;

    @Operation(summary = "카테고리별 가게 조회", description = "카테고리별 가게를 조회합니다.")
    @GetMapping("/{categoryId}/stores")
    public ResponseEntity<List<StoreResponseDto>> getStoresByCategory(
        @PathVariable UUID categoryId
    ) {
        List<StoreResponseDto> stores = storeService.getStoresByCategory(categoryId);
        return ResponseEntity.ok(stores);
    }
}
