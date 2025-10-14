package com.sparta.foodorder.domain.store.application;

import com.sparta.foodorder.domain.store.application.dto.StoreCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.StoreResponseDto;
import com.sparta.foodorder.domain.store.application.dto.StoreUpdateRequestDto;
import com.sparta.foodorder.domain.store.domain.*;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {

    private final StoreRepository storeRepository;
    private final CategoryRepository categoryRepository;

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
    public void deleteStore(UUID storeId, String username, String email, UserRole role) {
        validateOwnerRole(role);

        Store store = storeRepository.findById(storeId)
            .filter(s -> !s.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

        if (!store.getCreatedBy().equals(username)) {
            throw new BusinessException(ErrorCode.STORE_PERMISSION_DENIED);
        }

        store.softDelete(username);
    }

    @Override
    public List<StoreResponseDto> getStores(String query) {
        return null;
    }

    @Override
    public StoreResponseDto getStore(UUID storeId) {
        return null;
    }

    @Override
    public List<StoreResponseDto> getStoresByCategory(UUID categoryId) {
        return null;
    }

    @Override
    public Store findByUUID(UUID storeId) {
        return storeRepository.findById(storeId)
            .filter(s -> !s.isDeleted())
            .orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));
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
}
