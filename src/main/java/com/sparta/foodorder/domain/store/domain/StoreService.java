package com.sparta.foodorder.domain.store.domain;

import com.sparta.foodorder.domain.store.application.dto.StoreCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.StoreResponseDto;
import com.sparta.foodorder.domain.store.application.dto.StoreUpdateRequestDto;
import com.sparta.foodorder.domain.user.domain.UserRole;

import java.util.List;
import java.util.UUID;

public interface StoreService {

    /**
     * 가게 생성 메서드
     */
    StoreResponseDto createStore(
            StoreCreateRequestDto storeCreateRequestDto,
            Long userId,
            UserRole role
    );

    /**
     * 가게 수정 메서드
     */
    StoreResponseDto updateStore(
            StoreUpdateRequestDto storeUpdateRequestDto,
            UUID storeId,
            Long userId,
            UserRole role
    );

    /**
     * 가게 삭제 메서드
     */
    void deleteStore(UUID storeId, Long userId, UserRole role);

    /**
     * 가게목록 조회 메서드
     * 검색 키워드 있을 경우, 키워드 검색 결과 가게 목록 조회
     */
    List<StoreResponseDto> getStores(String query);

    /**
     * 가게 상세조회 메서드
     */
    StoreResponseDto getStore(UUID storeId);

    /**
     * 카테고리별 가게 조회 메서드
     */
    List<StoreResponseDto> getStoresByCategory(UUID categoryId);

    Store findByUUID(UUID storeId);

    void validateExistenceById(UUID storeId);
}
