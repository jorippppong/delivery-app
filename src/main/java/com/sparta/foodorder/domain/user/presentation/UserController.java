package com.sparta.foodorder.domain.user.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.user.application.dto.*;
import com.sparta.foodorder.domain.user.domain.UserService;
import com.sparta.foodorder.global.dto.PagedResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "email": "user@example.com",
                                      "username": "홍길동",
                                      "role": "USER",
                                      "status": "ACTIVE",
                                      "createdAt": "2024-01-15T10:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다",
                              "errors": {
                                "email": "이메일 형식이 올바르지 않습니다",
                                "password": "비밀번호는 8자 이상이어야 합니다"
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "EMAIL_ALREADY_EXISTS",
                              "message": "이미 사용 중인 이메일입니다"
                            }
                            """)))
    })
    @PostMapping
    public ResponseEntity<UserResponseDto> signup(@Valid @RequestBody SignupRequestDto request) {
        UserResponseDto response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "오너 회원 가입", description = "가게 오너(OWNER)로 회원가입합니다. 사업자등록번호 필수")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "오너 회원가입 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 2,
                                      "email": "owner@example.com",
                                      "username": "김사장",
                                      "role": "OWNER",
                                      "status": "ACTIVE",
                                      "businessNumber": "123-45-67890",
                                      "createdAt": "2024-01-15T11:00:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다",
                              "errors": {
                                "businessNumber": "사업자등록번호 형식이 올바르지 않습니다"
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "409", description = "이메일 또는 사업자등록번호 중복",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "BUSINESS_NUMBER_ALREADY_EXISTS",
                              "message": "이미 사용 중인 사업자등록번호입니다"
                            }
                            """)))
    })
    @PostMapping("/owner")
    public ResponseEntity<UserResponseDto> signupOwner(@Valid @RequestBody OwnerSignupRequestDto request) {
        UserResponseDto response = userService.signupOwner(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "매니저 회원 가입", description = "매니저(MANAGER)로 회원가입합니다. 관리자 승인이 필요합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "매니저 가입 신청 성공 (승인 대기 상태)",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 3,
                                      "email": "manager@example.com",
                                      "username": "박매니저",
                                      "role": "MANAGER",
                                      "status": "PENDING",
                                      "createdAt": "2024-01-15T12:00:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "EMAIL_ALREADY_EXISTS",
                              "message": "이미 사용 중인 이메일입니다"
                            }
                            """)))
    })
    @PostMapping("/manager")
    public ResponseEntity<UserResponseDto> signupManager(@Valid @RequestBody ManagerSignupRequestDto request) {
        UserResponseDto response = userService.signupManager(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "마스터 회원 가입", description = "마스터(MASTER)로 회원가입합니다. 관리자 승인이 필요합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "마스터 가입 신청 성공 (승인 대기 상태)",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 4,
                                      "email": "master@example.com",
                                      "username": "최마스터",
                                      "role": "MASTER",
                                      "status": "PENDING",
                                      "createdAt": "2024-01-15T13:00:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "409", description = "이메일 중복",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "EMAIL_ALREADY_EXISTS",
                              "message": "이미 사용 중인 이메일입니다"
                            }
                            """)))
    })
    @PostMapping("/master")
    public ResponseEntity<UserResponseDto> signupMaster(@Valid @RequestBody MasterSignupRequestDto request) {
        UserResponseDto response = userService.signupMaster(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 조회합니다 (본인만 가능)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "내 정보 조회 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "email": "user@example.com",
                                      "username": "홍길동",
                                      "role": "USER",
                                      "status": "ACTIVE",
                                      "phoneNumber": "010-1234-5678",
                                      "createdAt": "2024-01-15T10:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """)))
    })
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        UserResponseDto response = userService.getUser(userDetails.getUsername());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 프로필을 수정합니다 (본인만 가능)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "프로필 수정 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "email": "user@example.com",
                                      "username": "홍길동",
                                      "role": "USER",
                                      "status": "ACTIVE",
                                      "phoneNumber": "010-9999-8888",
                                      "createdAt": "2024-01-15T10:30:00",
                                      "updatedAt": "2024-01-20T14:25:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다",
                              "errors": {
                                "phoneNumber": "전화번호 형식이 올바르지 않습니다"
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """)))
    })
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> updateProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdateProfileRequestDto request) {
        UserResponseDto response = userService.updateProfile(userDetails.getUsername(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "비밀번호 변경", description = "현재 로그인한 사용자의 비밀번호를 변경합니다 (본인만 가능)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 현재 비밀번호 불일치 또는 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_PASSWORD",
                              "message": "현재 비밀번호가 일치하지 않습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """)))
    })
    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequestDto request) {
        userService.updatePassword(userDetails.getUsername(), request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원 탈퇴", description = "현재 로그인한 사용자를 비활성화합니다 (본인만 가능)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "회원 탈퇴 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """)))
    })
    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deactivateUser(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        userService.deactivateUser(userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    // ===== 관리자 전용 API =====

    @Operation(summary = "승인 대기 사용자 목록 조회", description = "승인 대기 중인 사용자 목록을 조회합니다 (MASTER 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인 대기 목록 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "content": [
                                {
                                  "id": 3,
                                  "email": "manager@example.com",
                                  "username": "박매니저",
                                  "role": "MANAGER",
                                  "status": "PENDING",
                                  "createdAt": "2024-01-15T12:00:00"
                                },
                                {
                                  "id": 4,
                                  "email": "master@example.com",
                                  "username": "최마스터",
                                  "role": "MASTER",
                                  "status": "PENDING",
                                  "createdAt": "2024-01-15T13:00:00"
                                }
                              ],
                              "page": 0,
                              "size": 20,
                              "totalElements": 2,
                              "totalPages": 1
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "권한 없음 - MASTER 역할 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "접근 권한이 없습니다"
                            }
                            """)))
    })
    @GetMapping("/pending")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<PagedResponse<UserResponseDto>> getPendingUsers(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<UserResponseDto> users = userService.getPendingUsers(pageable);
        return ResponseEntity.ok(PagedResponse.of(users));
    }

    @Operation(summary = "사용자 승인/거부", description = "승인 대기 중인 사용자를 승인 또는 거부합니다 (MASTER 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "승인/거부 처리 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 3,
                                      "email": "manager@example.com",
                                      "username": "박매니저",
                                      "role": "MANAGER",
                                      "status": "ACTIVE",
                                      "createdAt": "2024-01-15T12:00:00",
                                      "updatedAt": "2024-01-16T09:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "승인 상태 값이 올바르지 않습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "권한 없음 - MASTER 역할 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "접근 권한이 없습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "USER_NOT_FOUND",
                              "message": "사용자를 찾을 수 없습니다"
                            }
                            """)))
    })
    @PostMapping("/{userId}/approval")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<UserResponseDto> approveUser(
            @PathVariable Long userId,
            @Valid @RequestBody ApprovalRequestDto request) {
        UserResponseDto response = userService.approveUser(userId, request);
        return ResponseEntity.ok(response);
    }
}
