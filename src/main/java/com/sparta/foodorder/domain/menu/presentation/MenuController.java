package com.sparta.foodorder.domain.menu.presentation;


import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.menu.application.MenuService;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuCreateRequestDto;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuResponseDto;
import com.sparta.foodorder.domain.store.application.StoreServiceImpl;
import com.sparta.foodorder.domain.store.domain.StoreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/v1/stores/{storeId}/menus")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MenuController {
    private final MenuService menuService;

    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuResponseDto> createMenu(@PathVariable UUID storeId, @RequestBody @Valid MenuCreateRequestDto requestDto,
                                                      @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUserId();

        MenuResponseDto responseDto = menuService.insertMenu(requestDto, storeId, userId);

        return ResponseEntity.ok(responseDto);

    }
}
