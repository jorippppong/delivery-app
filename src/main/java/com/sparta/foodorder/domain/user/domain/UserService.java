package com.sparta.foodorder.domain.user.domain;

import com.sparta.foodorder.domain.user.application.dto.SignupRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdatePasswordRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdateProfileRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UserResponseDto;

public interface UserService {
    
    /**
     * 회원 가입
     */
    UserResponseDto signup(SignupRequestDto request);
    
    /**
     * 사용자 조회
     */
    UserResponseDto getUser(String userEmail);
    
    /**
     * 프로필 수정
     */
    UserResponseDto updateProfile(String userEmail, UpdateProfileRequestDto request);
    
    /**
     * 비밀번호 변경
     */
    void updatePassword(String userEmail, UpdatePasswordRequestDto request);
    
    /**
     * 사용자 비활성화
     */
    void deactivateUser(String userEmail);
}

