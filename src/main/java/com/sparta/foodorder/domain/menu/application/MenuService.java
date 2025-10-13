package com.sparta.foodorder.domain.menu.application;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.menu.domain.MenuRepository;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuCreateRequestDto;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuResponseDto;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class MenuService {

    private final MenuRepository menuRepository;
    private final StoreService storeService;

    public MenuResponseDto insertMenu(MenuCreateRequestDto requestDto, UUID storeId, Long userId) {
        log.info("로그인한 사용자 id : {}",userId);
        Store store = storeService.findById(storeId);

        //가게가 delete상태가 아니고, active상태일 때만 메뉴생성 가능하도록
        if(!store.isDeleted() && store.getIsActive()) {

            store.validateOwner(userId); //요청한 사용자가 가게 주인이 맞는지 검증

            Menu menu = requestDto.toEntity(store);
            Menu savedMenu = menuRepository.save(menu);
            MenuResponseDto responseDto = MenuResponseDto.from(savedMenu);
            return responseDto;
        } else throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
    }



}
