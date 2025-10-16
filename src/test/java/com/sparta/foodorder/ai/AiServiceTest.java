package com.sparta.foodorder.ai;

import com.sparta.foodorder.domain.ai.application.AiService;
import com.sparta.foodorder.domain.ai.application.PromptBuilder;
import com.sparta.foodorder.domain.ai.application.dto.AiRequestDto;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;
import com.sparta.foodorder.domain.ai.domain.AiLog;
import com.sparta.foodorder.domain.ai.domain.AiProvider;
import com.sparta.foodorder.domain.ai.infrastructure.AiLogRepository;
import com.sparta.foodorder.global.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AiService 테스트")
public class AiServiceTest {

    @Mock
    private AiProvider geminiProvider;

    @Mock
    private PromptBuilder promptBuilder;

    @Mock
    private AiLogRepository aiLogRepository;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        when(geminiProvider.getProviderName()).thenReturn("gemini");

        aiService = new AiService(
                List.of(geminiProvider),
                promptBuilder,
                aiLogRepository
        );

        ReflectionTestUtils.setField(aiService, "defaultProvider", "gemini");
    }

    @Test
    @DisplayName("AI 컨텐츠 생성 성공 - Gemini")
    void generateContent_Success_WithGemini() {
        // given
        Long userId = 1L;
        String productName = "김치찌개";
        String content = "김치를 기반으로 하는 찌개 요리";
        String prompt = "매콤하고 얼큰한 김치찌개입니다.";

        AiResponseDto expectedResponse = AiResponseDto.builder()
                .generatedText(prompt)
                .provider("gemini")
                .model("gemini-2.5-flash")
                .build();

        when(promptBuilder.buildProductGeneratePrompt(productName, content))
                .thenReturn(prompt);

        when(geminiProvider.generateContent(any(AiRequestDto.class)))
                .thenReturn(expectedResponse);

        when(aiLogRepository.save(any(AiLog.class)))
                .thenAnswer(invocationOnMock -> invocationOnMock.getArgument(0));

        // when
        AiResponseDto result = aiService.generateContent(userId, productName, content, "gemini");

        // then
        assertThat(result).isNotNull();
        assertThat(result.getGeneratedText()).isEqualTo("매콤하고 얼큰한 김치찌개입니다.");
        verify(promptBuilder, times(1)).buildProductGeneratePrompt(productName, content);
        verify(geminiProvider, times(1)).generateContent(any(AiRequestDto.class));
        verify(aiLogRepository, times(1)).save(any(AiLog.class));
    }

    @Test
    @DisplayName("지원하지 않는 AI Provider 예외 발생")
    void generateContent_Exception_ProviderNotFound() {
        // given
        Long userId = 1L;
        String productName = "김치찌개";
        String content = "김치를 기반으로 하는 찌개 요리";
        String invalidProvider = "invalid-provider";

        when(promptBuilder.buildProductGeneratePrompt(productName, content))
                .thenReturn("test prompt");

        // when & then
        assertThrows(BusinessException.class, () -> {
            aiService.generateContent(userId, productName, content, invalidProvider);
        });

        verify(aiLogRepository, never()).save(any(AiLog.class));
    }
}
