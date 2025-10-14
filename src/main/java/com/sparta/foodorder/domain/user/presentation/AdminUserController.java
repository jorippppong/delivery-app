package com.sparta.foodorder.domain.user.presentation;

import com.sparta.foodorder.domain.user.application.dto.UserResponseDto;
import com.sparta.foodorder.domain.user.application.dto.UserStatusUpdateRequestDto;
import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "관리자 - 사용자 관리", description = "관리자 전용 사용자 관리 API (MASTER, MANAGER)")
@RestController
@RequestMapping("/v1/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('MASTER', 'MANAGER')")
public class AdminUserController {

    private final UserService userService;

    @Operation(summary = "사용자 목록 조회", description = "모든 사용자 목록을 페이징하여 조회합니다 (관리자 전용)")
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserResponseDto> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다 (관리자 전용)")
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(UserResponseDto.from(user));
    }

    @Operation(summary = "사용자 상태 변경", description = "사용자 상태를 변경합니다 (ACTIVE/BANNED/WITHDRAWN)")
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('MASTER')")  
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequestDto request) {
        UserResponseDto response = userService.updateUserStatus(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 강제 탈퇴", description = "사용자를 강제로 탈퇴 처리합니다")
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('MASTER')")  
    public ResponseEntity<Void> forceDeleteUser(@PathVariable Long userId) {
        userService.forceDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "제재된 사용자 목록 조회", description = "제재(BANNED) 상태인 사용자 목록을 조회합니다")
    @GetMapping("/banned")
    public ResponseEntity<Page<UserResponseDto>> getBannedUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserResponseDto> users = userService.getBannedUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "탈퇴한 사용자 목록 조회", description = "탈퇴(WITHDRAWN) 상태인 사용자 목록을 조회합니다")
    @GetMapping("/withdrawn")
    public ResponseEntity<Page<UserResponseDto>> getWithdrawnUsers(
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<UserResponseDto> users = userService.getWithdrawnUsers(pageable);
        return ResponseEntity.ok(users);
    }
}

