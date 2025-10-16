package com.sparta.foodorder.domain.store.application;

import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.menu.presentation.dto.MenuResponseDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryResponseDto;
import com.sparta.foodorder.domain.store.application.dto.StoreCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.StoreDetailResponseDto;
import com.sparta.foodorder.domain.store.application.dto.StoreResponseDto;
import com.sparta.foodorder.domain.store.application.dto.StoreUpdateRequestDto;
import com.sparta.foodorder.domain.store.domain.Category;
import com.sparta.foodorder.domain.store.domain.CategoryRepository;
import com.sparta.foodorder.domain.store.domain.CategoryService;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreCategory;
import com.sparta.foodorder.domain.store.domain.StoreRepository;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.domain.store.infrastructure.StoreCategoryRepository;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.dto.PagedResponse;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;
    private final StoreCategoryRepository storeCategoryRepository;
    private final ReviewRepository reviewRepository;
    private final CategoryService categoryService;

    @Override
    @Transactional
    public StoreResponseDto createStore(
        StoreCreateRequestDto requestDto,
        Long userId,
        UserRole role
    ) {
        validateOwnerRole(role);

        if (storeRepository.existsByName(requestDto.name())) {
            throw new BusinessException(ErrorCode.STORE_ALREADY_EXIST);
        }
        if (storeRepository.existsByOwnerId(userId)) {
            throw new BusinessException(ErrorCode.OWNER_ALREADY_HAS_STORE);
        }
        if (storeRepository.existsByPhoneNumber(requestDto.phoneNumber())) {
            throw new BusinessException(ErrorCode.PHONE_ALREADY_EXIST);
        }

        Set<UUID> categoryIds = requestDto.categories();
        List<Category> categories = categoryRepository.findByIdIn(categoryIds);
        if (categoryIds.size() != categories.size()) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Store store = Store.createStore(
            userId, requestDto.name(), requestDto.description(), requestDto.address(),
            requestDto.longitude(), requestDto.latitude(), requestDto.phoneNumber(),
            requestDto.minOrderAmount(), requestDto.deliveryFee(),
            requestDto.opensAt(), requestDto.closesAt()
        );
        categories.forEach(store::addCategory);

        Store savedStore = storeRepository.save(store);

        return StoreResponseDto.from(savedStore);
    }

    @Override
    @Transactional
    public StoreResponseDto updateStore(
        StoreUpdateRequestDto requestDto,
        UUID storeId,
        String email,
        UserRole role
    ) {
        validateOwnerRole(role);
        Store store = getValidStore(storeId);
        if (!store.getCreatedBy().equals(email)) {
            throw new BusinessException(ErrorCode.STORE_PERMISSION_DENIED);
        }

        store.updateStore(requestDto.name(), requestDto.description(), requestDto.address(),
            requestDto.longitude(), requestDto.latitude(), requestDto.phoneNumber(),
            requestDto.isActive(), requestDto.minOrderAmount(), requestDto.deliveryFee(),
            requestDto.opensAt(), requestDto.closesAt());

        updateCategories(store, requestDto.categories());

        return StoreResponseDto.from(store);
    }

    @Override
    @Transactional
    public void deleteStore(UUID storeId, String email, UserRole role) {
        validateOwnerRole(role);

        Store store = storeRepository.findById(storeId)
            .filter(s -> !s.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getCreatedBy().equals(email)) {
            throw new BusinessException(ErrorCode.STORE_PERMISSION_DENIED);
        }

        store.softDelete(email);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StoreResponseDto> getStores(String query, Pageable pageable) {
        Page<Store> storePage;

        if (!StringUtils.hasText(query)) {
            storePage = storeRepository.findAllByIsActiveTrue(pageable);
        } else {
            storePage = storeRepository.findAllByNameContainingAndIsActiveTrue(query, pageable);
        }

        List<StoreResponseDto> data = storePage.getContent().stream()
            .map(StoreResponseDto::from)
            .toList();

        return PagedResponse.success(data, storePage.getNumber(), storePage.getSize(),
            storePage.hasNext());
    }

    @Override
    @Transactional(readOnly = true)
    public StoreDetailResponseDto getStore(UUID storeId) {
        Store store = getValidStore(storeId);
        List<CategoryResponseDto> categoryResponseDtoList = categoryService.getCategoryDtoList(
            store);
        List<MenuResponseDto> menuResponseDtoList = getMenuDtoList(store);

        return StoreDetailResponseDto.from(store, categoryResponseDtoList, menuResponseDtoList);
    }

    @Override
    @Transactional(readOnly = true)
    public PagedResponse<StoreResponseDto> getStoresByCategory(UUID categoryId, Pageable pageable) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
        }

        Page<StoreCategory> storeCategories =
            storeCategoryRepository.findAllByCategoryIdAndStoreIsActiveTrue(categoryId, pageable);
        List<StoreResponseDto> data = storeCategories.getContent().stream()
            .map(StoreCategory::getStore)
            .map(StoreResponseDto::from).toList();

        return PagedResponse.success(data, storeCategories.getNumber(), storeCategories.getSize(),
            storeCategories.hasNext());
    }

    @Override
    public Store findByUUID(UUID storeId) {
        return storeRepository.findById(storeId)
            .filter(s -> !s.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
    }

    @Override
    public void validateExistenceById(UUID storeId) {
        if (!storeRepository.existsByIdAndIsActiveTrue(storeId)) {
            throw new BusinessException(ErrorCode.STORE_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public Store updateRating(UUID storeId) {
        Store store = storeRepository.findByIdAndIsActiveTrue(storeId)
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
        RatingStats stats = reviewRepository.calculateRatingStats(storeId);
        store.updateRating(stats.getCount(), stats.getAverage());
        return storeRepository.save(store);
    }

    private void updateCategories(Store store, Set<UUID> categories) {
        if (categories != null && !categories.isEmpty()) {
            store.getStoreCategories().clear();
            categories.forEach(categoryId -> {
                Category category = categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));

                store.addCategory(category);
            });
        }
    }

    private Store getValidStore(UUID storeId) {
        return storeRepository.findById(storeId)
            .filter(s -> !s.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
    }

    private void validateOwnerRole(UserRole role) {
        if (role != UserRole.OWNER) {
            throw new BusinessException(ErrorCode.STORE_PERMISSION_DENIED);
        }
    }

    private List<MenuResponseDto> getMenuDtoList(Store store) {
        List<Menu> menuList = store.getMenus().stream().toList();
        List<MenuResponseDto> menuResponseDtoList = new ArrayList<>();
        for (Menu menu : menuList) {
            menuResponseDtoList.add(MenuResponseDto.from(menu));
        }

        return menuResponseDtoList;
    }
}
