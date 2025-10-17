package com.sparta.foodorder.domain.address.presentation;

import com.sparta.foodorder.domain.address.application.dto.AddressRequestDto;
import com.sparta.foodorder.domain.address.application.dto.AddressResponseDto;
import com.sparta.foodorder.domain.address.domain.AddressService;
import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "배송지", description = "배송지 관련 API")
@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class AddressController {

    private final AddressService addressService;

    @Operation(summary = "배송지 등록", description = "현재 로그인한 사용자의 새로운 배송지를 등록합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "배송지 등록 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "userId": 10,
                                      "recipientName": "홍길동",
                                      "recipientPhone": "010-1234-5678",
                                      "addressType": "HOME",
                                      "roadAddress": "서울시 강남구 테헤란로 123",
                                      "detailAddress": "101호",
                                      "zipCode": "06234",
                                      "isDefault": true,
                                      "createdAt": "2024-01-15T10:30:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다",
                              "errors": {
                                "recipientPhone": "전화번호 형식이 올바르지 않습니다"
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
    @PostMapping
    public ResponseEntity<AddressResponseDto> createAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddressRequestDto request) {
        AddressResponseDto response = addressService.createAddress(userDetails.getUserId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "내 배송지 목록 조회", description = "현재 로그인한 사용자의 모든 배송지를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 목록 조회 성공",
                    content = @Content(examples = @ExampleObject(value = """
                            [
                              {
                                "id": 1,
                                "userId": 10,
                                "recipientName": "홍길동",
                                "recipientPhone": "010-1234-5678",
                                "addressType": "HOME",
                                "roadAddress": "서울시 강남구 테헤란로 123",
                                "detailAddress": "101호",
                                "zipCode": "06234",
                                "isDefault": true,
                                "createdAt": "2024-01-15T10:30:00"
                              },
                              {
                                "id": 2,
                                "userId": 10,
                                "recipientName": "김철수",
                                "recipientPhone": "010-9876-5432",
                                "addressType": "OFFICE",
                                "roadAddress": "서울시 서초구 강남대로 456",
                                "detailAddress": "5층",
                                "zipCode": "06789",
                                "isDefault": false,
                                "createdAt": "2024-01-16T14:20:00"
                              }
                            ]
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """)))
    })
    @GetMapping
    public ResponseEntity<List<AddressResponseDto>> getUserAddresses(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<AddressResponseDto> responses = addressService.getUserAddresses(userDetails.getUserId());
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "배송지 상세 조회", description = "배송지 상세 정보를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 조회 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "userId": 10,
                                      "recipientName": "홍길동",
                                      "recipientPhone": "010-1234-5678",
                                      "addressType": "HOME",
                                      "roadAddress": "서울시 강남구 테헤란로 123",
                                      "detailAddress": "101호",
                                      "zipCode": "06234",
                                      "isDefault": true,
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
            @ApiResponse(responseCode = "403", description = "권한 없음 - 다른 사용자의 배송지",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "해당 배송지에 접근할 권한이 없습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "ADDRESS_NOT_FOUND",
                              "message": "배송지를 찾을 수 없습니다"
                            }
                            """)))
    })
    @GetMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> getAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        AddressResponseDto response = addressService.getAddress(addressId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배송지 수정", description = "배송지 정보를 수정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "배송지 수정 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "userId": 10,
                                      "recipientName": "홍길동",
                                      "recipientPhone": "010-1111-2222",
                                      "addressType": "HOME",
                                      "roadAddress": "서울시 강남구 테헤란로 789",
                                      "detailAddress": "202호",
                                      "zipCode": "06234",
                                      "isDefault": true,
                                      "createdAt": "2024-01-15T10:30:00",
                                      "updatedAt": "2024-01-20T15:45:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 - 유효성 검증 실패",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "INVALID_INPUT",
                              "message": "입력값이 올바르지 않습니다",
                              "errors": {
                                "zipCode": "우편번호는 5자리 숫자여야 합니다"
                              }
                            }
                            """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 다른 사용자의 배송지",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "해당 배송지를 수정할 권한이 없습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "ADDRESS_NOT_FOUND",
                              "message": "배송지를 찾을 수 없습니다"
                            }
                            """)))
    })
    @PutMapping("/{addressId}")
    public ResponseEntity<AddressResponseDto> updateAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId,
            @Valid @RequestBody AddressRequestDto request) {
        AddressResponseDto response = addressService.updateAddress(addressId, userDetails.getUserId(), request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "배송지 삭제", description = "배송지를 삭제합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "배송지 삭제 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 다른 사용자의 배송지",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "해당 배송지를 삭제할 권한이 없습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "ADDRESS_NOT_FOUND",
                              "message": "배송지를 찾을 수 없습니다"
                            }
                            """)))
    })
    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        addressService.deleteAddress(addressId, userDetails.getUserId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "기본 배송지 설정", description = "기본 배송지로 설정합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기본 배송지 설정 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 2,
                                      "userId": 10,
                                      "recipientName": "김철수",
                                      "recipientPhone": "010-9876-5432",
                                      "addressType": "OFFICE",
                                      "roadAddress": "서울시 서초구 강남대로 456",
                                      "detailAddress": "5층",
                                      "zipCode": "06789",
                                      "isDefault": true,
                                      "createdAt": "2024-01-16T14:20:00",
                                      "updatedAt": "2024-01-22T09:15:00"
                                    }
                                    """))),
            @ApiResponse(responseCode = "401", description = "인증 실패 - 로그인 필요",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "UNAUTHORIZED",
                              "message": "인증이 필요합니다"
                            }
                            """))),
            @ApiResponse(responseCode = "403", description = "권한 없음 - 다른 사용자의 배송지",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "FORBIDDEN",
                              "message": "해당 배송지를 수정할 권한이 없습니다"
                            }
                            """))),
            @ApiResponse(responseCode = "404", description = "배송지를 찾을 수 없음",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "ADDRESS_NOT_FOUND",
                              "message": "배송지를 찾을 수 없습니다"
                            }
                            """)))
    })
    @PatchMapping("/{addressId}/default")
    public ResponseEntity<AddressResponseDto> setDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long addressId) {
        AddressResponseDto response = addressService.setDefaultAddress(addressId, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "기본 배송지 조회", description = "현재 로그인한 사용자의 기본 배송지를 조회합니다")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "기본 배송지 조회 성공",
                    content = @Content(schema = @Schema(implementation = AddressResponseDto.class),
                            examples = @ExampleObject(value = """
                                    {
                                      "id": 1,
                                      "userId": 10,
                                      "recipientName": "홍길동",
                                      "recipientPhone": "010-1234-5678",
                                      "addressType": "HOME",
                                      "roadAddress": "서울시 강남구 테헤란로 123",
                                      "detailAddress": "101호",
                                      "zipCode": "06234",
                                      "isDefault": true,
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
            @ApiResponse(responseCode = "404", description = "기본 배송지가 없음",
                    content = @Content(examples = @ExampleObject(value = """
                            {
                              "code": "DEFAULT_ADDRESS_NOT_FOUND",
                              "message": "기본 배송지가 설정되지 않았습니다"
                            }
                            """)))
    })
    @GetMapping("/default")
    public ResponseEntity<AddressResponseDto> getDefaultAddress(
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        AddressResponseDto response = addressService.getDefaultAddress(userDetails.getUserId());
        return ResponseEntity.ok(response);
    }
}
