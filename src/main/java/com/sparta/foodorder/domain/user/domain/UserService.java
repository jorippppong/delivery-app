package com.sparta.foodorder.domain.user.domain;

import com.sparta.foodorder.domain.user.application.dto.SignupRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdatePasswordRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdateProfileRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UserResponseDto;
import com.sparta.foodorder.domain.user.application.dto.UserStatusUpdateRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    
    /**
     * 회원 가입
     */
    UserResponseDto signup(SignupRequestDto request);
    
    /**
     * 사용자 조회 (이메일로)
     */
    UserResponseDto getUser(String userEmail);
    
    /**
     * 사용자 조회 (ID로) - Service 레이어에서 공통 사용
     */
    User findUserById(Long userId);
    
    /**
     * 사용자 조회 (이메일로) - Service 레이어에서 공통 사용
     */
    User findUserByEmail(String userEmail);
    
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
    
    // ===== 관리자 전용 메서드 =====
    
    /**
     * 모든 사용자 목록 조회 (페이징)
     */
    Page<UserResponseDto> getAllUsers(Pageable pageable);
    
    /**
     * 사용자 상태 변경 (관리자)
     */
    UserResponseDto updateUserStatus(Long userId, UserStatusUpdateRequestDto request);
    
    /**
     * 사용자 강제 탈퇴 (관리자)
     */
    void forceDeleteUser(Long userId);
    
    /**
     * 제재된 사용자 목록 조회
     */
    Page<UserResponseDto> getBannedUsers(Pageable pageable);
    
    /**
     * 탈퇴한 사용자 목록 조회
     */
    Page<UserResponseDto> getWithdrawnUsers(Pageable pageable);
}

