package com.sparta.foodorder.domain.user.presentation;

import com.sparta.foodorder.domain.user.application.dto.UserResponseDto;
import com.sparta.foodorder.domain.user.application.dto.UserStatusUpdateRequestDto;
import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserService;
import com.sparta.foodorder.global.util.PageableUtils;
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
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 목록 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "content": [
                                {
                                  "id": 1,
                                  "email": "user@example.com",
                                  "username": "홍길동",
                                  "role": "USER",
                                  "status": "ACTIVE",
                                  "createdAt": "2024-01-15T10:30:00"
                                },
                                {
                                  "id": 2,
                                  "email": "owner@example.com",
                                  "username": "김사장",
                                  "role": "OWNER",
                                  "status": "ACTIVE",
                                  "createdAt": "2024-01-15T11:00:00"
                                }
                              ],
                              "pageable": {
                                "pageNumber": 0,
                                "pageSize": 20
                              },
                              "totalElements": 50,
                              "totalPages": 3
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 관리자 역할 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "접근 권한이 없습니다"
                            }
                            """)))
    })
    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable validatedPageable = PageableUtils.validateAndAdjust(pageable);
        Page<UserResponseDto> users = userService.getAllUsers(validatedPageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "사용자 상세 조회", description = "특정 사용자의 상세 정보를 조회합니다 (관리자 전용)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 조회 성공",
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
                            """))),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 관리자 역할 필요",
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
    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long userId) {
        User user = userService.findUserById(userId);
        return ResponseEntity.ok(UserResponseDto.from(user));
    }

    @Operation(summary = "사용자 상태 변경", description = "사용자 상태를 변경합니다 (ACTIVE/BANNED/WITHDRAWN)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 상태 변경 성공",
                    content = @Content(schema = @Schema(implementation = UserResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 5,
                                      "email": "baduser@example.com",
                                      "username": "제재대상",
                                      "role": "USER",
                                      "status": "BANNED",
                                      "createdAt": "2024-01-10T08:00:00",
                                      "updatedAt": "2024-01-20T16:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효하지 않은 상태 값",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "유효하지 않은 상태 값입니다"
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
    @PatchMapping("/{userId}/status")
    @PreAuthorize("hasRole('MASTER')")  
    public ResponseEntity<UserResponseDto> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody UserStatusUpdateRequestDto request) {
        UserResponseDto response = userService.updateUserStatus(userId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "사용자 강제 탈퇴", description = "사용자를 강제로 탈퇴 처리합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "사용자 강제 탈퇴 성공"),
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
    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('MASTER')")  
    public ResponseEntity<Void> forceDeleteUser(@PathVariable Long userId) {
        userService.forceDeleteUser(userId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "제재된 사용자 목록 조회", description = "제재(BANNED) 상태인 사용자 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "제재 사용자 목록 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "content": [
                                {
                                  "id": 5,
                                  "email": "baduser1@example.com",
                                  "username": "제재유저1",
                                  "role": "USER",
                                  "status": "BANNED",
                                  "createdAt": "2024-01-10T08:00:00",
                                  "updatedAt": "2024-01-20T16:30:00"
                                },
                                {
                                  "id": 7,
                                  "email": "baduser2@example.com",
                                  "username": "제재유저2",
                                  "role": "USER",
                                  "status": "BANNED",
                                  "createdAt": "2024-01-12T10:00:00",
                                  "updatedAt": "2024-01-21T09:15:00"
                                }
                              ],
                              "pageable": {
                                "pageNumber": 0,
                                "pageSize": 20
                              },
                              "totalElements": 12,
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
            @ApiResponse(responseCode = "403", description = "권한 없음 - 관리자 역할 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "접근 권한이 없습니다"
                            }
                            """)))
    })
    @GetMapping("/banned")
    public ResponseEntity<Page<UserResponseDto>> getBannedUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable validatedPageable = PageableUtils.validateAndAdjust(pageable);
        Page<UserResponseDto> users = userService.getBannedUsers(validatedPageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "탈퇴한 사용자 목록 조회", description = "탈퇴(WITHDRAWN) 상태인 사용자 목록을 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "탈퇴 사용자 목록 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "content": [
                                {
                                  "id": 15,
                                  "email": "withdrawn1@example.com",
                                  "username": "탈퇴유저1",
                                  "role": "USER",
                                  "status": "WITHDRAWN",
                                  "createdAt": "2023-12-01T08:00:00",
                                  "updatedAt": "2024-01-18T14:20:00"
                                },
                                {
                                  "id": 18,
                                  "email": "withdrawn2@example.com",
                                  "username": "탈퇴유저2",
                                  "role": "USER",
                                  "status": "WITHDRAWN",
                                  "createdAt": "2023-11-15T10:00:00",
                                  "updatedAt": "2024-01-19T11:30:00"
                                }
                              ],
                              "pageable": {
                                "pageNumber": 0,
                                "pageSize": 20
                              },
                              "totalElements": 8,
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
            @ApiResponse(responseCode = "403", description = "권한 없음 - 관리자 역할 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "접근 권한이 없습니다"
                            }
                            """)))
    })
    @GetMapping("/withdrawn")
    public ResponseEntity<Page<UserResponseDto>> getWithdrawnUsers(
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        Pageable validatedPageable = PageableUtils.validateAndAdjust(pageable);
        Page<UserResponseDto> users = userService.getWithdrawnUsers(validatedPageable);
        return ResponseEntity.ok(users);
    }
}

