package com.sparta.foodorder.domain.ai.presentation;

import com.sparta.foodorder.domain.ai.application.dto.AiResponseDto;
import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "AI", description = "AI 관련 API")
public interface AiApiDocs {

    @Operation(summary = "AI 메뉴 설명 생성", description = "AI 메뉴 설명을 생성합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                           "generatedText": "고소한 참깨빵, 육즙 패티, 특별 소스! 완벽 조화로 탄생한 수제버거의 정석.",
                                           "provider": "gemini",
                                           "model": "gemini-2.5-flash"
                                         }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 403,
                                          "code": "C006",
                                          "message": "접근이 거부되었습니다",
                                          "timestamp": "2025-10-16T17:04:12.7543232"
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "code": "U001",
                                          "message": "사용자를 찾을 수 없습니다",
                                          "timestamp": "2025-10-16T17:04:12.7543232"
                                        }
                                    """)
                    )
            ),
    })
    ResponseEntity<AiResponseDto> aiGenerate(
            @Parameter(description = "음식명") String productName,
            @Parameter(description = "음식 설명") String content,
            @Parameter(description = "AI 모델") String provider,
            CustomUserDetails userDetails
    );
}
