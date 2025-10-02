package com.sparta.foodorder.domain.ai.domain;

import com.sparta.foodorder.domain.ai.application.dto.AiRequestDto;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;

public interface AiProvider {
    AiResponseDto generateContent(AiRequestDto aiRequestDto);
    String getProviderName();
}
