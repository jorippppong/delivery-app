package com.sparta.foodorder.domain.ai.application;

import com.sparta.foodorder.domain.ai.application.dto.AiRequestDto;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;
import com.sparta.foodorder.domain.ai.domain.AiProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AiService {

    private final Map<String, AiProvider> providers;

    @Value("${ai.default-provider:gemini}")
    private String defaultProvider;

    public AiService(List<AiProvider> providerList) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(AiProvider::getProviderName, Function.identity()));
    }

    public AiResponseDto generateContent(String prompt) {
        return generateContent(prompt, defaultProvider);
    }

    public AiResponseDto generateContent(String prompt, String providerName) {
        AiRequestDto request = AiRequestDto.builder()
                .prompt(prompt)
                .build();
        return generateContent(request, providerName);
    }

    public AiResponseDto generateContent(AiRequestDto request, String providerName) {
        AiProvider provider = providers.get(providerName);

        if (provider == null) {
            log.error("지원하지 않는 AI Provider: {}", providerName);
            throw new IllegalArgumentException("지원하지 않는 AI Provider: " + providerName);
        }

        log.info("AI 컨텐츠 생성 요청 - Provider: {}, Prompt: {}", providerName, request.getPrompt());
        return provider.generateContent(request);
    }
}
