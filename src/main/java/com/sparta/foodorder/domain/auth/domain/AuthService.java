package com.sparta.foodorder.domain.auth.domain;

import com.sparta.foodorder.domain.auth.application.dto.LoginRequestDto;
import com.sparta.foodorder.domain.auth.application.dto.LoginResponseDto;
import com.sparta.foodorder.domain.user.domain.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Optional;

public interface AuthService {
    
    /**
     * 로그인 처리
     */
    LoginResponseDto login(LoginRequestDto requestDto);
    
    /**
     * 이메일로 사용자 조회 (권한 정보 포함)
     */
    Optional<User> findByEmailWithRoles(String userEmail);
    
    /**
     * 로그아웃 처리 (쿠키 기반)
     */
    String logout(HttpServletRequest request, HttpServletResponse response);
    
    /**
     * 토큰 갱신 처리
     */
    LoginResponseDto refresh(String refreshToken);
}
