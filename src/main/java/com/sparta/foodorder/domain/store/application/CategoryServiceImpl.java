package com.sparta.foodorder.domain.store.application;

import com.sparta.foodorder.domain.store.application.dto.CategoryCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryResponseDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryUpdateRequestDto;
import com.sparta.foodorder.domain.store.domain.Category;
import com.sparta.foodorder.domain.store.domain.CategoryRepository;
import com.sparta.foodorder.domain.store.domain.CategoryService;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreCategory;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.exception.BusinessException;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.sparta.foodorder.global.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public CategoryResponseDto createCategory(
        CategoryCreateRequestDto requestDto,
        Long userId,
        UserRole role
    ) {
        validateCategoryPermission(role);
        if(categoryRepository.existsByName(requestDto.name())) {
            throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXIST);
        }

        Category category = Category.createCategory(requestDto.name());
        Category savedCategory = categoryRepository.save(category);

        return CategoryResponseDto.from(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(
        UUID categoryId,
        CategoryUpdateRequestDto requestDto,
        Long userId,
        UserRole role
    ) {
        validateCategoryPermission(role);
        Category category = categoryRepository.findById(categoryId)
            .orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_NOT_FOUND));
        category.updateCategory(requestDto.name());

        return CategoryResponseDto.from(category);
    }

    @Override
    public List<CategoryResponseDto> getCategoryDtoList(Store store) {
        List<Category> categoryList = store.getStoreCategories().stream()
            .map(StoreCategory::getCategory).toList();
        List<CategoryResponseDto> categoryResponseDtoList = new ArrayList<>();
        for(Category category : categoryList) {
            categoryResponseDtoList.add(CategoryResponseDto.from(category));
        }
        return categoryResponseDtoList;
    }

    private void validateCategoryPermission(UserRole role) {
        if(role == UserRole.USER || role == UserRole.OWNER) {
            throw new BusinessException(ErrorCode.CATEGORY_PERMISSION_DENIED);
        }
    }
}
