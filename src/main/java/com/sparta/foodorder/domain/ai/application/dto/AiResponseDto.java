package com.sparta.foodorder.domain.ai.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiResponseDto {
    private String generatedText;
    private String provider;
    private String model;
}