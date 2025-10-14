package com.sparta.foodorder.domain.menu.presentation;


import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.menu.application.MenuService;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuCreateRequestDto;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuResponseDto;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuUpdateRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "메뉴", description = "메뉴 API")
@RequestMapping("/v1/stores/{storeId}/menus")
@RestController
@RequiredArgsConstructor
@Slf4j
public class MenuController {

    private final MenuService menuService;

    @Operation(summary = "메뉴 생성", description = "새로운 메뉴를 생성합니다.")
    @PostMapping
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<MenuResponseDto> createMenu(@PathVariable UUID storeId,
        @RequestBody @Valid MenuCreateRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails user) {

        Long userId = user.getUserId();

        MenuResponseDto responseDto = menuService.insertMenu(requestDto, storeId, userId);

        return ResponseEntity.ok(responseDto);

    }

    @Operation(summary = "전체 메뉴 조회", description = "해당 가게의 모든 메뉴를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MenuResponseDto>> getAllMenus(@PathVariable UUID storeId,
        @AuthenticationPrincipal CustomUserDetails user) {
        List<MenuResponseDto> menuList = menuService.getMenus(storeId, user);
        return ResponseEntity.ok(menuList);
    }

    @Operation(summary = "단일 메뉴 조회", description = "해당 가게의 특정 메뉴를 조회합니다.")
    @GetMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> getMenu(@PathVariable UUID storeId,
        @PathVariable UUID menuId,
        @AuthenticationPrincipal CustomUserDetails user) {
        String userRole = user.getRole().toString();
        if (userRole.equals("USER")) {
            log.info("메뉴 단일 조회 - 유저");
            MenuResponseDto responseDto = menuService.getMenuForUser(storeId, menuId, user);
            return ResponseEntity.ok(responseDto);

        } else {
            log.info("메뉴 단일 조회 - 사장");
            MenuResponseDto responseDto = menuService.getMenuForOwner(storeId, menuId,
                user); //표현계층에 서비스가 의존. menuId만 반환후 조회해서 가져오는 것이 좋음
            return ResponseEntity.ok(responseDto);
        }
    }

    @Operation(summary = "메뉴 수정", description = "해당 가게의 특정 메뉴를 수정합니다.")
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{menuId}")
    public ResponseEntity<MenuResponseDto> updateMenu(@PathVariable UUID storeId,
        @PathVariable UUID menuId,
        @RequestBody @Valid MenuUpdateRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails user) {
        Long userId = user.getUserId();
        MenuResponseDto responseDto = menuService.updateMenu(storeId, menuId, requestDto, userId);

        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "메뉴 삭제", description = "해당 가게의 특정 메뉴를 삭제합니다.")
    @PreAuthorize("hasAnyRole('OWNER','MANAGER','ADMIN')")
    @DeleteMapping("/{menuId}")
    public ResponseEntity<Void> deleteMenu(@PathVariable UUID storeId,
        @PathVariable UUID menuId,
        @AuthenticationPrincipal CustomUserDetails user) {
        menuService.deleteMenu(storeId, menuId, user);
        return ResponseEntity.ok().build();
    }

}
