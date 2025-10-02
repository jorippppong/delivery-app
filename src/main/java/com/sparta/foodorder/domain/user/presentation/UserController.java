package com.sparta.foodorder.domain.user.presentation;

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

    @Operation(summary = "사용자 조회", description = "사용자 정보를 조회합니다")
    @GetMapping("/{userEmail}")
    public ResponseEntity<UserResponseDto> getUser(@PathVariable String userEmail) {
        UserResponseDto response = userService.getUser(userEmail);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 수정", description = "사용자 프로필을 수정합니다")
    @PutMapping("/{userEmail}")
    public ResponseEntity<UserResponseDto> updateProfile(
            @PathVariable String userEmail,
            @Valid @RequestBody UpdateProfileRequestDto request) {
        UserResponseDto response = userService.updateProfile(userEmail, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경", description = "사용자 비밀번호를 변경합니다")
    @PutMapping("/{userEmail}/password")
    public ResponseEntity<Void> updatePassword(
            @PathVariable String userEmail,
            @Valid @RequestBody UpdatePasswordRequestDto request) {
        userService.updatePassword(userEmail, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자 비활성화", description = "사용자를 비활성화합니다")
    @DeleteMapping("/{userEmail}")
    public ResponseEntity<Void> deactivateUser(@PathVariable String userEmail) {
        userService.deactivateUser(userEmail);
        return ResponseEntity.noContent().build();
    }
}
