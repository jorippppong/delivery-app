package com.sparta.foodorder.domain.ai.infrastructure.client.gemini;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sparta.foodorder.domain.ai.application.dto.AiRequestDto;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;
import com.sparta.foodorder.domain.ai.domain.AiProvider;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class GeminiClient implements AiProvider {

    private final WebClient.Builder webClientBuilder;
    private final ObjectMapper objectMapper;

    @Value("${ai.providers.gemini.api-key}")
    private String apiKey;
    @Value("${ai.providers.gemini.api-url}")
    private String apiUrl;
    @Value("${ai.providers.gemini.model}")
    private String model;
    @Value("${ai.providers.gemini.generate-content-api}")
    private String generateContentApi;

    @Override
    public AiResponseDto generateContent(AiRequestDto aiRequestDto) {
        log.info("Gemini API 호출 시작 - prompt: {}", aiRequestDto.getPrompt());

        try {
            GeminiRequestDto geminiRequestDto = new GeminiRequestDto();
            geminiRequestDto.createRequestDto(aiRequestDto.getPrompt());

            String rawResponse = webClientBuilder.build()
                    .post()
                    .uri(apiUrl + model + ":" + generateContentApi + "?key=" + apiKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(geminiRequestDto)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            List<GeminiResponseDto> responseDtoList = objectMapper.readValue(rawResponse, new TypeReference<>() {
            });

            if (responseDtoList == null || responseDtoList.isEmpty()) {
                throw new BusinessException(ErrorCode.AI_EMPTY_RESPONSE);
            }

            String generatedText = extractGeneratedText(responseDtoList.get(0));

            log.info("Gemini API 호출 성공 - 생성된 텍스트 : {}", generatedText);

            return AiResponseDto.builder()
                    .generatedText(generatedText)
                    .provider(getProviderName())
                    .model(model)
                    .build();

        } catch (Exception e) {
            log.error("Gemini API 호출 실패", e);
            throw new BusinessException(ErrorCode.AI_GEMINI_API_ERROR);
        }
    }

    @Override
    public String getProviderName() {
        return "gemini";
    }

    private String extractGeneratedText(GeminiResponseDto responseDto) {
        if (responseDto == null || responseDto.getCandidates() == null || responseDto.getCandidates().isEmpty())
            throw new BusinessException(ErrorCode.AI_EMPTY_RESPONSE);

        return responseDto.getCandidates().get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }
}
