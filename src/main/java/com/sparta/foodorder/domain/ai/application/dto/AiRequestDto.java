package com.sparta.foodorder.domain.ai.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiRequestDto {
    private String prompt;
    private String model;
    private Integer maxTokens;
    private Double temperature;
}