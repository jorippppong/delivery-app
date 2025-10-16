package com.sparta.foodorder.domain.menu.presentation.dto;

import lombok.Getter;

@Getter
public class OptionValueUpdateRequestDto {
    String value;
    String description;
    Integer addPrice;
}
