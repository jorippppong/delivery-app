package com.sparta.foodorder.domain.ai.domain;

import com.sparta.foodorder.domain.ai.application.dto.AiRequestDto;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;

public interface AiProvider {
    AiResponseDto getAiResponseDto(AiRequestDto aiRequestDto);
    String getProviderName();
}
