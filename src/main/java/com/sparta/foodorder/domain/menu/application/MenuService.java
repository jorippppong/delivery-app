package com.sparta.foodorder.domain.menu.application;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.menu.domain.MenuRepository;
import com.sparta.foodorder.domain.menu.domain.Option;
import com.sparta.foodorder.domain.menu.presentation.dto.*;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreRepository;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.dto.PagedResponse;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j

public class MenuService {
    private final MenuRepository menuRepository;
    private final StoreService storeService;
    private final StoreRepository storeRepository;

    public MenuResponseDto insertMenu(MenuCreateRequestDto requestDto, UUID storeId, Long userId) {
        log.info("로그인한 사용자 id : {}", userId);
        Store store = storeService.findByUUID(storeId);

        //가게가 delete상태가 아니고, active상태일 때만 메뉴생성 가능하도록
        if (!store.isDeleted() && store.getIsActive()) {

            store.validateOwner(userId); //요청한 사용자가 가게 주인이 맞는지 검증

            Menu menu = requestDto.toEntity(store);
            Menu savedMenu = menuRepository.save(menu);
            MenuResponseDto responseDto = MenuResponseDto.from(savedMenu);
            return responseDto;
        } else {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
    }

    @Transactional
    public MenuResponseDto createOption(OptionCreateRequestDto requestDto, UUID menuId, CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        boolean isOwner = storeRepository.findByOwnerId(userId).isPresent();
        boolean isMasterOrManager = checkAdminAuthorization(userDetails);

        //#1 . 생성 권한이 있나 확인
        if(!isOwner && ! isMasterOrManager) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
        Store store;
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        if(isOwner) {
            //#2. 오너면 가게 주인이 맞나 확인
            store = storeRepository.findByOwnerId(userId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

            if(store.isDeleted()) {
                throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
            }
            if(!menu.getStore().getId().equals(store.getId())) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
            }

            return saveOption(menu, requestDto);
        }

        UUID storeId = menu.getStore().getId();
        store =  storeRepository.findById(storeId).orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        if(store.isDeleted()) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }

        return saveOption(menu, requestDto);
    }

    @Transactional(readOnly = true)
    public List<MenuResponseDto> getMenus(UUID storeId, CustomUserDetails user) {
        Store store = storeService.findByUUID(storeId);
        List<Menu> menu;
        UserRole userRole = user.getRole();

        if(userRole == UserRole.MANAGER||userRole == UserRole.MASTER) {
            menu = menuRepository.findByStoreId(storeId);

        } else if(userRole == UserRole.USER || !store.getOwnerId().equals(user.getUserId())) {
            menu = menuRepository.findByStoreIdAndActiveTrueAndHiddenFalseAndDeletedAtIsNull(storeId);

        } else {
            menu = menuRepository.findByStoreIdAndDeletedAtIsNull(storeId);
        }

        if (menu.isEmpty()) {
            return Collections.emptyList();
        }

        return menu.stream().map(MenuResponseDto::from).toList();
    }


    @Transactional(readOnly = true)
    public MenuResponseDto getMenuForUser(UUID storeId, UUID menuId, CustomUserDetails user) {

        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        Store store = storeService.findByUUID(storeId);

        //해당 가게의 메뉴가 맞는지 확인
        if (menu.getStore().getId().equals(storeId)) {
            //메뉴가 활성화 상태인지 확인
            if (!menu.isActive() || menu.isHidden() || menu.isDeleted()) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
            }

        } else throw new BusinessException(ErrorCode.MENU_NOT_FOUND); //가게정보가 일치하지 않으면 예외처리
        return MenuResponseDto.from(menu);
    }


    @Transactional(readOnly = true)
    public MenuResponseDto getMenuForOwner(UUID storeId, UUID menuId, CustomUserDetails user) {

        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        Store store = storeService.findByUUID(storeId);
        log.info("가게 id : {}",storeId);
        UserRole userRole = user.getRole();
        //해당 가게의 메뉴가 맞는지 확인
        if (!menu.getStore().getId().equals(storeId)) {
            throw new BusinessException(ErrorCode.STORE_PERMISSION_DENIED);
        }

        if (store.getOwnerId().equals(user.getUserId()) || userRole ==UserRole.MANAGER || userRole ==UserRole.MASTER) {
            if(menu.isDeleted()) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
            } else return MenuResponseDto.from(menu);

        } else throw new BusinessException(ErrorCode.MENU_NOT_FOUND); //가게정보가 일치하지 않으면 예외처리

    }

    public MenuResponseDto updateMenu(UUID storeId, UUID menuId,
        @Valid MenuUpdateRequestDto requestDto, Long userId) {
        //가게 존재 및 소유자 검증
        Store store = storeService.findByUUID(storeId);
        store.validateOwner(userId);

        //메뉴 존재 검증
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        if(menu.isDeleted()) throw new BusinessException(ErrorCode.MENU_NOT_FOUND);

        //메뉴가 해당 가게 아래에 있는 메뉴가 맞는지
        if (menu.getStore().getId().equals(storeId)) {
            menu.changeMenu(
                requestDto.getName(),
                requestDto.getDescription(),
                requestDto.getPrice(),
                requestDto.isHidden(),
                requestDto.isActive()
            );
            Menu savedMenu = menuRepository.save(menu);
            MenuResponseDto responseDto = MenuResponseDto.from(savedMenu);

            return responseDto;
        } else throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
    }

    public void deleteMenu(UUID storeId, UUID menuId, CustomUserDetails user) {
        //삭제할 메뉴 조회
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        String username = user.getUsername();
        UserRole userRole = user.getRole();

        if(menu.isDeleted()) throw new BusinessException(ErrorCode.MENU_NOT_FOUND); //이미 삭제된 상품입니다 errorCode 추가하자

        if(menu.getStore().getId().equals(storeId)) { //해당 가게의 메뉴가 맞는지 확인
        if(userRole ==UserRole.MASTER || userRole ==UserRole.MANAGER) {
                menu.deleteMenu(username);
                menuRepository.save(menu);
        } else {
            //삭제 요청한 사용자가 가게 주인이 맞는지 검증
            Store store = storeService.findByUUID(storeId);
            store.validateOwner(user.getUserId());
             menu.deleteMenu(username);
             menuRepository.save(menu);
            }
        }
    }

    //메뉴가 주문 가능한 상태인지(활성화되어있는지 체크)
    @Transactional(readOnly = true)
    public Menu OrderableMenu(UUID menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        if(!menu.isActive() || menu.isHidden() || menu.isDeleted()) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        return menu;
    }

    public List<Menu> findAllByIds(List<UUID> menuIds) {
        return menuRepository.findAllByIds(menuIds);
    }

    public boolean checkAdminAuthorization (CustomUserDetails userDetails) {
        UserRole userRole = userDetails.getRole();
        return userRole == UserRole.MASTER || userRole == UserRole.MANAGER;
    }

    private MenuResponseDto saveOption(Menu menu, OptionCreateRequestDto requestDto) {
        Option option = requestDto.toEntity(menu);
        menu.getOptions().add(option);
        menuRepository.saveAndFlush(menu);
        log.info("option : {}", option.getId());
        return MenuResponseDto.from(menu);
    }

    public PagedResponse<MenuSearchResponseDto> searchMenus(String searchString, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<Menu> menuPage = menuRepository.findByNameContaining(searchString, pageable);
        log.info("menuPage 데이터 존재? : {} " , menuPage.toString());
        List<MenuSearchResponseDto> menuSearchResponseDtoList = menuPage.getContent().stream()
                .filter(menu -> !menu.isDeleted())
                .filter(menu -> menu.isActive())
                .filter(menu -> !menu.isHidden())
                .map(MenuSearchResponseDto::fromEntity)
                .toList();

        boolean hasNext = menuPage.hasNext();
        return PagedResponse.success(menuSearchResponseDtoList, page,size, hasNext);
    }


}
