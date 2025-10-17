package com.sparta.foodorder.domain.ai.presentation;

import com.sparta.foodorder.domain.ai.application.AiService;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;
import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('OWNER', 'MASTER', 'MANAGER')")
public class AiGenerationController implements AiApiDocs {

    private final AiService aiService;

    @PostMapping("/menus/ai-content")
    public ResponseEntity<AiResponseDto> aiGenerate(
            @RequestParam String productName,
            @RequestParam(required = false) String content,
            @RequestParam(required = false, defaultValue = "gemini") String provider,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        if (userDetails == null)
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);

        AiResponseDto responseDto = aiService.generateContent(userDetails.getUserId(), productName, content, provider);
        return ResponseEntity.ok(responseDto);
    }
}
