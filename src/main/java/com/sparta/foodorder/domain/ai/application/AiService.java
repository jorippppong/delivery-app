package com.sparta.foodorder.domain.ai.application;

import com.sparta.foodorder.domain.ai.application.dto.AiRequestDto;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;
import com.sparta.foodorder.domain.ai.domain.AiLog;
import com.sparta.foodorder.domain.ai.domain.AiProvider;
import com.sparta.foodorder.domain.ai.infrastructure.AiLogRepository;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
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
    private final PromptBuilder promptBuilder;
    private final AiLogRepository aiLogRepository;

    @Value("${ai.default-provider:gemini}")
    private String defaultProvider;

    public AiService(List<AiProvider> providerList, PromptBuilder promptBuilder, AiLogRepository aiLogRepository) {
        this.providers = providerList.stream()
                .collect(Collectors.toMap(AiProvider::getProviderName, Function.identity()));
        this.promptBuilder = promptBuilder;
        this.aiLogRepository = aiLogRepository;
    }

    public AiResponseDto generateContent(Long userId, String productName, String content, String providerName) {
        String prompt = promptBuilder.buildProductGeneratePrompt(productName, content);

        AiRequestDto request = AiRequestDto.builder()
                .userId(userId)
                .productName(productName)
                .content(content)
                .prompt(prompt)
                .build();
        return generateContent(request, providerName);
    }

    public AiResponseDto generateContent(AiRequestDto request, String providerName) {
        AiProvider provider = providers.get(providerName);

        if (provider == null) {
            log.error("지원하지 않는 AI Provider: {}", providerName);
            throw new BusinessException(ErrorCode.AI_PROVIDER_NOT_FOUND);
        }

        log.info("AI 컨텐츠 생성 요청 - Provider: {}", providerName);
        AiResponseDto aiResponseDto = provider.generateContent(request);

        AiLog aiLog = AiLog.builder()
                .userId(request.getUserId())
                .aiModel(aiResponseDto.getModel())
                .productName(request.getProductName())
                .requestContent(request.getContent())
                .requestType(AiLog.RequestType.MENU_DESCRIPTION)
                .responseContent(aiResponseDto.getGeneratedText())
                .status(AiLog.Status.SUCCESS)
                .build();

        aiLogRepository.save(aiLog);
        return aiResponseDto;
    }
}
