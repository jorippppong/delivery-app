package com.sparta.foodorder.domain.store.domain;

import com.sparta.foodorder.domain.store.application.dto.CategoryCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryResponseDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryUpdateRequestDto;
import com.sparta.foodorder.domain.user.domain.UserRole;
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
}
