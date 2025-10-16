package com.sparta.foodorder.domain.user.domain;

import com.sparta.foodorder.domain.user.application.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface UserService {

    /**
     * 일반 회원 가입 (USER)
     */
    UserResponseDto signup(SignupRequestDto request);

    /**
     * 오너 회원 가입 (OWNER)
     * 사업자등록번호 검증 포함
     */
    UserResponseDto signupOwner(OwnerSignupRequestDto request);

    /**
     * 매니저 회원 가입 (MANAGER)
     * 승인 대기 상태로 생성
     */
    UserResponseDto signupManager(ManagerSignupRequestDto request);

    /**
     * 마스터 회원 가입 (MASTER)
     * 승인 대기 상태로 생성
     */
    UserResponseDto signupMaster(MasterSignupRequestDto request);

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

    /**
     * 승인 대기 중인 사용자 목록 조회
     */
    Page<UserResponseDto> getPendingUsers(Pageable pageable);

    /**
     * 사용자 승인/거부 (MASTER 전용)
     */
    UserResponseDto approveUser(Long userId, ApprovalRequestDto request);

    List<User> findAllByIdIn(Set<Long> userIds);
}

