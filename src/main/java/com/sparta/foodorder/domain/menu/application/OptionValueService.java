package com.sparta.foodorder.domain.menu.application;

import com.sparta.foodorder.domain.menu.domain.OptionValue;
import com.sparta.foodorder.domain.menu.domain.OptionValueRepository;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptionValueService {
    private final OptionValueRepository optionValueRepository;

    public OptionValue findById(UUID valueId) {
        return optionValueRepository.findById(valueId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_OPTION_VALUE_NOT_FOUND));
    }
}
