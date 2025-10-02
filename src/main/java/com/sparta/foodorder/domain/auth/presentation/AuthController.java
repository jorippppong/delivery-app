package com.sparta.foodorder.domain.auth.presentation;

import io.swagger.v3.oas.annotations.Operation;
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
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto requestDto) {
        LoginResponseDto response = authService.login(requestDto);
        return ResponseEntity.ok(response);
    }
    
    @DeleteMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃합니다")
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String result = authService.logout(request, response);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/refresh")
    @Operation(summary = "토큰 갱신", description = "RefreshToken으로 새로운 AccessToken을 발급받습니다")
    public ResponseEntity<LoginResponseDto> refresh(@RequestHeader("Authorization") String refreshToken) {
        LoginResponseDto response = authService.refresh(refreshToken);
        return ResponseEntity.ok(response);
    }
}
