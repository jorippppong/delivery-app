package com.sparta.foodorder.domain.menu.presentation.dto;

import com.sparta.foodorder.domain.menu.domain.OptionValue;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class OptionValueResponseDto {
    private final UUID id;
    private final String value;
    private final String description;
    private final Integer addPrice;

    private OptionValueResponseDto(OptionValue optionValue) {
        this.id = optionValue.getId();
        this.value = optionValue.getValue();
        this.description = optionValue.getDescription();
        this.addPrice = optionValue.getAddPrice();
    }

    public static List<OptionValueResponseDto> findAllOptionValues(List<OptionValue>  optionValues) {
        return optionValues.stream().map(OptionValueResponseDto::new)
                .toList();
    }
}
