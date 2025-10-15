package com.sparta.foodorder.domain.menu.application;

import com.sparta.foodorder.domain.menu.domain.Option;
import com.sparta.foodorder.domain.menu.domain.OptionRepository;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;

    public Option findById(UUID optionId) {
        return optionRepository.findById(optionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MENU_OPTION_NOT_FOUND));

    }
}
