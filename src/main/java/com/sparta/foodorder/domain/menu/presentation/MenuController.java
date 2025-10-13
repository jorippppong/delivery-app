package com.sparta.foodorder.domain.menu.presentation;


import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.menu.application.MenuService;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuCreateRequestDto;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping
    public ResponseEntity<List<MenuResponseDto>> getAllMenus(@PathVariable UUID storeId,
                                                             @AuthenticationPrincipal CustomUserDetails user) {
        List<MenuResponseDto> menuList = menuService.getMenus(storeId, user);
        return ResponseEntity.ok(menuList);
    }

    //TODO 단일 메뉴 조회
    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> getMenu(@PathVariable UUID storeId,
                                                   @PathVariable UUID menuId,
                                                   @AuthenticationPrincipal CustomUserDetails user) {
        String userRole = user.getUser().getRole().toString();
        if(userRole.equals("USER")) {
            log.info("메뉴 단일 조회 - 유저");
            MenuResponseDto responseDto = menuService.getMenuForUser(storeId, menuId, user);
            return ResponseEntity.ok(responseDto);

        } else {
            log.info("메뉴 단일 조회 - 사장");
            MenuResponseDto responseDto = menuService.getMenuForOwner(storeId, menuId, user); //표현계층에 서비스가 의존. menuId만 반환후 조회해서 가져오는 것이 좋음
            return ResponseEntity.ok(responseDto);
        }
    }
}
