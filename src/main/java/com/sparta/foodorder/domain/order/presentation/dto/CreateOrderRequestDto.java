package com.sparta.foodorder.domain.order.presentation.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public record CreateOrderRequestDto(
        @NotNull
        UUID storeId,
        List<MenuInfo> menus,
        @NotBlank
        String addressLine,
        @NotBlank
        String detailAddress,
        @Size(max = 200)
        String memo
) {
    public record MenuInfo(
            UUID menuId,
            @Min(1)
            int quantity,
            List<OptionInfo> options
    ) {

    }

    public record OptionInfo(
            UUID optionId,
            List<UUID> optionValueIds
    ) {

    }

    @JsonIgnore
    public List<UUID> getMenuIds() {
        return menus().stream()
                .map(MenuInfo::menuId)
                .collect(Collectors.toList());
    }

    @JsonIgnore
    public Map<UUID, Integer> getMenuIdAndQuantity() {
        return menus.stream()
                .collect(Collectors.toMap(
                        MenuInfo::menuId,
                        MenuInfo::quantity,
                        Integer::sum
                ));
    }
}

