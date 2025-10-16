package com.sparta.foodorder.domain.store.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CategoryUpdateRequestDto(
    @Schema(description = "카테고리명", example = "패스트푸드")
    String name
) {

}