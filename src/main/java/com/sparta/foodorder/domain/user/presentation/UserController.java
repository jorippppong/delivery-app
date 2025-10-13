package com.sparta.foodorder.domain.user.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.user.application.dto.*;
import com.sparta.foodorder.domain.user.domain.UserService;
import com.sparta.foodorder.global.dto.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "사용자", description = "사용자 관련 API")
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "일반 회원 가입", description = "일반 사용자(USER)로 회원가입합니다")
    @PostMapping
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        UserResponseDto response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "오너 회원 가입", description = "가게 오너(OWNER)로 회원가입합니다. 사업자등록번호 필수")
    @PostMapping("/owner")
    public ResponseEntity<UserResponseDto> signupOwner(@Valid @RequestBody OwnerSignupRequestDto request) {
        UserResponseDto response = userService.signupOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "매니저 회원 가입", description = "매니저(MANAGER)로 회원가입합니다. 관리자 승인이 필요합니다")
    @PostMapping("/manager")
    public ResponseEntity<UserResponseDto> signupManager(@Valid @RequestBody ManagerSignupRequestDto request) {
        UserResponseDto response = userService.signupManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "마스터 회원 가입", description = "마스터(MASTER)로 회원가입합니다. 관리자 승인이 필요합니다")
    @PostMapping("/master")
    public ResponseEntity<UserResponseDto> signupMaster(@Valid @RequestBody MasterSignupRequestDto request) {
        UserResponseDto response = userService.signupMaster(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다 (본인만 가능)")
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponseDto response = userService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 프로필을 수정합니다 (본인만 가능)")
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequestDto request) {
        UserResponseDto response = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다 (본인만 가능)")
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequestDto request) {
        userService.updatePassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 비활성화합니다 (본인만 가능)")
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deactivateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deactivateUser(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ===== 관리자 전용 API =====

    @Operation(summary = "승인 대기 사용자 목록 조회", description = "승인 대기 중인 사용자 목록을 조회합니다 (MASTER 전용)")
    @GetMapping("/pending")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<PagedResponse<UserResponseDto>> getPendingUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponseDto> users = userService.getPendingUsers(pageable);
        return ResponseEntity.ok(PagedResponse.of(users));
    }

    @Operation(summary = "사용자 승인/거부", description = "승인 대기 중인 사용자를 승인 또는 거부합니다 (MASTER 전용)")
    @PostMapping("/{userId}/approval")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<UserResponseDto> approveUser(
            @PathVariable Long userId,
            @Valid @RequestBody ApprovalRequestDto request) {
        UserResponseDto response = userService.approveUser(userId, request);
        return ResponseEntity.ok(response);
    }
}
