package com.sparta.foodorder.domain.menu.presentation.dto;

import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.menu.domain.Option;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OptionCreateRequestDto {

    @NotBlank
    private String optionName;

    @Valid
    List<OptionValueCreateRequestDto> optionValues;

    public Option toEntity(Menu menu) {
        Option option = Option.create(
                menu,
                this.optionName,
                new ArrayList<>()
        );


        if(optionValues != null && !optionValues.isEmpty()) {
            //각 list에 있는 DTO를 하나씩 꺼내 엔티티로 변환 -> 옵션의 optionvalue리스트에 추가
            optionValues.forEach(optionValueDto ->
                    option.getOptionValues().add(optionValueDto.toEntity(option)));
        }

        return option;
    }
}
