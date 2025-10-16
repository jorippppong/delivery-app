package com.sparta.foodorder.domain.order.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Schema(description = "주문 생성 요청 DTO")
public record CreateOrderRequestDto(
        @Schema(description = "가게 ID", example = "a1b2c3d4-e5f6-47a8-b9c0-1234567890ab")
        @NotNull
        UUID storeId,

        @Schema(description = "주문할 메뉴 리스트")
        List<MenuInfo> menus,

        @Schema(description = "주소", example = "서울특별시 강남구 테헤란로 123")
        @NotBlank
        String addressLine,

        @Schema(description = "상세 주소", example = "101동 202호")
        @NotBlank
        String detailAddress,

        @Schema(description = "요청사항 (최대 200자)", example = "벨 누르지 말고 문자 주세요")
        @Size(max = 200)
        String memo
) {
    public record MenuInfo(
            @Schema(description = "메뉴 ID", example = "11111111-2222-3333-4444-555555555555")
            UUID menuId,

            @Schema(description = "수량", example = "2")
            @Min(1)
            int quantity,

            @Schema(description = "선택 옵션 리스트")
            List<OptionInfo> options
    ) {

    }

    public record OptionInfo(
            @Schema(description = "옵션 ID", example = "aaaa1111-bbbb-2222-cccc-333333333333")
            UUID optionId,

            @Schema(description = "선택한 옵션 값 ID 리스트", example = "[\"abcd1234-ef56-7890-ab12-34567890cdef\", \"def12345-6789-0123-4567-890abcdef123\"]")
            List<UUID> optionValueIds
    ) {

    }

    @JsonIgnore
    public List<UUID> getMenuIds() {
        return menus().stream()
                .map(MenuInfo::menuId)
                .collect(Collectors.toList());
    }
}

