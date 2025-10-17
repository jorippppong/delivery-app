package com.sparta.foodorder.domain.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.sparta.foodorder.domain.auth.application.AuthServiceImpl;
import com.sparta.foodorder.domain.auth.application.dto.LoginRequestDto;
import com.sparta.foodorder.domain.auth.application.dto.LoginResponseDto;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
@Tag(name = "인증", description = "인증 관련 API")
public class AuthController {
    
    private final AuthServiceImpl authService;
    
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
                                      "tokenType": "Bearer",
                                      "expiresIn": 3600,
                                      "userId": 1,
                                      "email": "user@example.com",
                                      "username": "홍길동",
                                      "role": "USER"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다",
                              "errors": {
                                "email": "이메일은 필수입니다"
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 이메일 또는 비밀번호 불일치",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_CREDENTIALS",
                              "message": "이메일 또는 비밀번호가 올바르지 않습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "계정 상태 이상 - BANNED 또는 WITHDRAWN",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "ACCOUNT_BANNED",
                              "message": "제재된 계정입니다"
                            }
                            """)))
    })
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response) {
        LoginResponseDto loginResponse = authService.login(requestDto, response);
        return ResponseEntity.ok(loginResponse);
    }
    
    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "로그아웃 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            "로그아웃되었습니다"
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 유효하지 않은 토큰",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_TOKEN",
                              "message": "유효하지 않은 토큰입니다"
                            }
                            """)))
    })
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String result = authService.logout(request, response);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "RefreshToken으로 새로운 AccessToken을 발급받습니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "토큰 갱신 성공",
                    content = @Content(schema = @Schema(implementation = LoginResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.new...",
                                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.new...",
                                      "tokenType": "Bearer",
                                      "expiresIn": 3600,
                                      "userId": 1,
                                      "email": "user@example.com",
                                      "username": "홍길동",
                                      "role": "USER"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - RefreshToken 누락",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "MISSING_TOKEN",
                              "message": "RefreshToken이 필요합니다"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 만료되거나 유효하지 않은 RefreshToken",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_REFRESH_TOKEN",
                              "message": "유효하지 않거나 만료된 RefreshToken입니다"
                            }
                            """)))
    })
    public ResponseEntity<LoginResponseDto> refresh(
            @RequestHeader("Authorization") String refreshToken,
            HttpServletResponse response) {
        LoginResponseDto loginResponse = authService.refresh(refreshToken, response);
        return ResponseEntity.ok(loginResponse);
    }
}
