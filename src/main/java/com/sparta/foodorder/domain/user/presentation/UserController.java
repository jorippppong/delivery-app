package com.sparta.foodorder.domain.user.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.user.application.dto.SignupRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdatePasswordRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UpdateProfileRequestDto;
import com.sparta.foodorder.domain.user.application.dto.UserResponseDto;
import com.sparta.foodorder.domain.user.domain.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @Operation(summary = "회원 가입", description = "새로운 사용자를 등록합니다")
    @PostMapping
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        UserResponseDto response = userService.signup(request);
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
}
