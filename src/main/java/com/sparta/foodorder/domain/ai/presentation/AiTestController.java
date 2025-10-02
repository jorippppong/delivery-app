package com.sparta.foodorder.domain.ai.presentation;

import com.sparta.foodorder.domain.ai.application.AiService;
import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ai/test")
@RequiredArgsConstructor
public class AiTestController {

    private final AiService aiService;

    @PostMapping("/generate")
    public AiResponseDto testGenerate(
            @RequestParam String prompt,
            @RequestParam(required = false, defaultValue = "gemini") String provider) {
        return aiService.generateContent(prompt, provider);
    }
}
