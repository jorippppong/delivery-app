package com.sparta.foodorder.domain.store.application.dto;

import com.sparta.foodorder.domain.store.domain.Category;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CategoryResponseDto(
    UUID id,
    String name
) {
    public static CategoryResponseDto from(Category category) {
        return CategoryResponseDto.builder()
            .id(category.getId())
            .name(category.getName())
            .build();
    }
}
