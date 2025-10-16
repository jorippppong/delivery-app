package com.sparta.foodorder.domain.ai.presentation;

import com.sparta.foodorder.domain.ai.application.AiService;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;
import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "AI", description = "AI 관련 API")
@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class AiGenerationController {

    private final AiService aiService;

    @Operation(summary = "AI 메뉴 설명 생성", description = "AI 메뉴 설명을 생성합니다.")
    @PostMapping("/menus/ai-content")
    public AiResponseDto aiGenerate(
            @RequestParam String productName,
            @RequestParam(required = false) String content,
            @RequestParam(required = false, defaultValue = "gemini") String provider,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null)
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        return aiService.generateContent(userDetails.getUserId(), productName, content, provider);
    }
}
