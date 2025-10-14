package com.sparta.foodorder.domain.menu.presentation.dto;

import com.sparta.foodorder.domain.menu.domain.Option;
import com.sparta.foodorder.domain.menu.domain.OptionValue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OptionValueCreateRequestDto {
    @NotBlank
    String value;

    String description;

    Integer addPrice;

    public OptionValue toEntity(Option option) {
        return OptionValue.create(
                option,
                this.value,
                this.description,
                this.addPrice
        );
    }

}
