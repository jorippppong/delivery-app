package com.sparta.foodorder.domain.menu.application;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.menu.domain.MenuRepository;
import com.sparta.foodorder.domain.menu.domain.Option;
import com.sparta.foodorder.domain.menu.domain.OptionRepository;
import com.sparta.foodorder.domain.menu.domain.OptionValue;
import com.sparta.foodorder.domain.menu.domain.OptionValueRepository;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuCreateRequestDto;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuResponseDto;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuUpdateRequestDto;
import com.sparta.foodorder.domain.menu.presentation.dto.OptionResponseDto;
import com.sparta.foodorder.domain.menu.presentation.dto.OptionUpdateRequestDto;
import com.sparta.foodorder.domain.menu.presentation.dto.OptionValueResponseDto;
import com.sparta.foodorder.domain.menu.presentation.dto.OptionValueUpdateRequestDto;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import jakarta.validation.Valid;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MenuService {

    private final MenuRepository menuRepository;
    private final OptionRepository optionRepository;
    private final OptionValueRepository optionValueRepository;
    private final StoreService storeService;

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

    @Transactional(readOnly = true)
    public List<MenuResponseDto> getMenus(UUID storeId, CustomUserDetails user) {
        Store store = storeService.findByUUID(storeId);
        List<Menu> menu;
        UserRole userRole = user.getRole();

        if (userRole == UserRole.MANAGER || userRole == UserRole.MASTER) {
            menu = menuRepository.findByStoreId(storeId);

        } else if (userRole == UserRole.USER || !store.getOwnerId().equals(user.getUserId())) {
            menu = menuRepository.findByStoreIdAndActiveTrueAndHiddenFalseAndDeletedAtIsNull(
                storeId);

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

        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        Store store = storeService.findByUUID(storeId);

        //해당 가게의 메뉴가 맞는지 확인
        if (menu.getStore().getId().equals(storeId)) {
            //메뉴가 활성화 상태인지 확인
            if (!menu.isActive() || menu.isHidden() || menu.isDeleted()) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
            }

        } else {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND); //가게정보가 일치하지 않으면 예외처리
        }
        return MenuResponseDto.from(menu);
    }


    @Transactional(readOnly = true)
    public MenuResponseDto getMenuForOwner(UUID storeId, UUID menuId, CustomUserDetails user) {

        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        Store store = storeService.findByUUID(storeId);
        log.info("가게 id : {}", storeId);
        UserRole userRole = user.getRole();
        //해당 가게의 메뉴가 맞는지 확인
        if (!menu.getStore().getId().equals(storeId)) {
            throw new BusinessException(ErrorCode.STORE_PERMISSION_DENIED);
        }

        if (store.getOwnerId().equals(user.getUserId()) || userRole == UserRole.MANAGER
            || userRole == UserRole.MASTER) {
            if (menu.isDeleted()) {
                throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
            } else {
                return MenuResponseDto.from(menu);
            }

        } else {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND); //가게정보가 일치하지 않으면 예외처리
        }

    }

    public MenuResponseDto updateMenu(UUID storeId, UUID menuId,
        @Valid MenuUpdateRequestDto requestDto, Long userId) {
        //가게 존재 및 소유자 검증
        Store store = storeService.findByUUID(storeId);
        store.validateOwner(userId);

        //메뉴 존재 검증
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        if (menu.isDeleted()) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }

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
        } else {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
    }

    public void deleteMenu(UUID storeId, UUID menuId, CustomUserDetails user) {
        //삭제할 메뉴 조회
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
        String username = user.getUsername();
        UserRole userRole = user.getRole();

        if (menu.isDeleted()) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND); //이미 삭제된 상품입니다 errorCode 추가하자
        }

        if (menu.getStore().getId().equals(storeId)) { //해당 가게의 메뉴가 맞는지 확인
            if (userRole == UserRole.MASTER || userRole == UserRole.MANAGER) {
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
        Menu menu = menuRepository.findById(menuId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));

        if (!menu.isActive() || menu.isHidden() || menu.isDeleted()) {
            throw new BusinessException(ErrorCode.MENU_NOT_FOUND);
        }
        return menu;
    }

    public List<Menu> findAllByIds(List<UUID> menuIds) {
        return menuRepository.findAllByIds(menuIds);
    }

    @Transactional
    public OptionResponseDto updateOption(
        OptionUpdateRequestDto requestDto,
        UUID menuId, UUID optionId, Long userId
    ) {
        Menu menu = getValidMenu(menuId);
        Store store = getValidStore(menu);
        validateOwner(store, userId);

        Option option = getValidOptionOfMenu(optionId, menuId);
        option.updateOption(requestDto.getOptionName());

        return OptionResponseDto.from(option);
    }

    @Transactional
    public void deleteOption(UUID menuId, UUID optionId, Long userId, String email) {
        Menu menu = getValidMenu(menuId);
        Store store = getValidStore(menu);
        validateOwner(store, userId);

        Option option = getValidOptionOfMenu(optionId, menuId);
        option.delete(email);
    }

    @Transactional
    public OptionValueResponseDto updateOptionValue(
        OptionValueUpdateRequestDto requestDto,
        UUID menuId, UUID optionId, UUID optionValueId, Long userId
    ) {
        Menu menu = getValidMenu(menuId);
        Store store = getValidStore(menu);
        validateOwner(store, userId);
        validateOptionInMenu(optionId, menuId);

        OptionValue optionValue = getValidOptionValueOfOption(optionValueId, optionId);
        optionValue.updateOptionValue(
            requestDto.getValue(), requestDto.getDescription(), requestDto.getAddPrice());

        return OptionValueResponseDto.from(optionValue);
    }

    @Transactional
    public void deleteOptionValue(
        UUID menuId, UUID optionId, UUID optionValueId, Long userId,
        String email
    ) {
        Menu menu = getValidMenu(menuId);
        Store store = getValidStore(menu);
        validateOwner(store, userId);
        validateOptionInMenu(optionId, menuId);

        OptionValue optionValue = getValidOptionValueOfOption(optionValueId, optionId);
        optionValue.delete(email);
    }

    private Menu getValidMenu(UUID menuId) {
        return menuRepository.findByIdAndDeletedAtIsNull(menuId)
            .orElseThrow(() -> new BusinessException(ErrorCode.MENU_NOT_FOUND));
    }

    private Store getValidStore(Menu menu) {
        Store store = menu.getStore();
        if (store.isDeleted()) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
        return store;
    }

    private Option getValidOptionOfMenu(UUID optionId, UUID menuId) {
        return optionRepository.findByIdAndMenu_IdAndDeletedAtIsNull(optionId, menuId)
            .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_NOT_FOUND));
    }

    private OptionValue getValidOptionValueOfOption(UUID optionValueId, UUID optionId) {
        return optionValueRepository.findByIdAndOption_IdAndDeletedAtIsNull(optionValueId, optionId)
            .orElseThrow(() -> new BusinessException(ErrorCode.OPTION_VALUE_NOT_FOUND));
    }

    private void validateOptionInMenu(UUID optionId, UUID menuId) {
        if (!optionRepository.existsByIdAndMenu_idAndDeletedAtIsNull(optionId, menuId)) {
            throw new BusinessException(ErrorCode.OPTION_NOT_FOUND);
        }
    }

    private void validateOwner(Store store, Long userId) {
        if (!Objects.equals(store.getOwnerId(), userId)) {
            throw new BusinessException(ErrorCode.STORE_PERMISSION_DENIED);
        }
    }
}
