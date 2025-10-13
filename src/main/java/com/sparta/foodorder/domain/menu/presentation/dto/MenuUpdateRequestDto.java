package com.sparta.foodorder.domain.menu.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MenuUpdateRequestDto {

    private String name;
    private String description;

    @NotNull
    @PositiveOrZero
    private Integer price;

    private boolean hidden;
    private boolean active;
}
