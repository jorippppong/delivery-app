package com.sparta.foodorder.domain.store.domain;

import com.sparta.foodorder.domain.store.application.dto.CategoryCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryResponseDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryUpdateRequestDto;
import com.sparta.foodorder.domain.user.domain.UserRole;
import java.util.List;
import java.util.UUID;

public interface CategoryService {

    /**
     * 카테고리 생성
     */
    CategoryResponseDto createCategory(
        CategoryCreateRequestDto requestDto,
        Long userId,
        UserRole role
    );

    /**
     * 카테고리 수정
     */
    CategoryResponseDto updateCategory(
        UUID categoryId,
        CategoryUpdateRequestDto requestDto,
        Long userId,
        UserRole role
    );

    /**
     * 특정 store에 대한 여러 개의 categoryResponseDto를 리스트 형태로 반환하는 메서드
     *
     * @return categoryResponseDto 리스트 형태로 반환
     */
    List<CategoryResponseDto> getCategoryDtoList(Store store);
}
