package com.sparta.foodorder.domain.store.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.store.application.dto.CategoryCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryResponseDto;
import com.sparta.foodorder.domain.store.application.dto.CategoryUpdateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.StoreResponseDto;
import com.sparta.foodorder.global.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@Tag(name = "카테고리", description = "카테고리 관련 API")
public interface CategoryApiDocs {

    @Operation(summary = "카테고리별 가게 조회", description = "카테고리별 가게 목록을 조회합니다. (생성일자 내림차순으로 정렬)")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = """
                            USER -> isActive가 True인 가게만 조회 가능
                            OWNER, MANAGER, MASTER -> 삭제되지 않은 가게는 모두 조회 가능 (isActive가 false도 조회 가능)
                        """,
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "data": [
                                                {
                                                    "storeId": "4bf54044-4ecb-4b98-b692-a2f6f70a5a7d",
                                                    "ownerId": 4,
                                                    "name": "최가네버섯샤브매운탕칼국수",
                                                    "description": "가성비 최고의 얼큰한 샤브샤브 세트",
                                                    "address": "서울 강남구 도산대로51길 36 2층3층",
                                                    "longitude": 127.0276,
                                                    "latitude": 37.4979,
                                                    "phoneNumber": "010-0000-0000",
                                                    "isActive": true,
                                                    "ratingCount": 100,
                                                    "ratingAvg": 4.8,
                                                    "deliveryFee": 5000,
                                                    "minOrderAmount": 20000,
                                                    "opensAt": "11:00:00",
                                                    "closesAt": "21:40:00",
                                                    "createdAt": "2025-10-15T00:18:41.118404",
                                                    "updatedAt": "2025-10-16T17:58:24.585354"
                                                }
                                            ],
                                            "page": 0,
                                            "size": 10,
                                            "hasNext": false
                                        }
                                    """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "code": "S006",
                                            "message": "카테고리를 찾을 수 없습니다.",
                                            "timestamp": "2025-10-16T18:05:48.5765081"
                                        }
                                    """)
            )
        )
    })
    public ResponseEntity<PagedResponse<StoreResponseDto>> getStoresByCategory(
        @PathVariable UUID categoryId,

        @ParameterObject
        @PageableDefault(size = 10)
        @SortDefault(sort = "createdAt", direction = Direction.DESC)
        Pageable pageable,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );




    @Operation(summary = "카테고리 생성", description = "관리자는 카테고리를 생성합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "id": "80fd1533-a151-11f0-982b-00155d41dd91",
                                            "name": "패스트푸드"
                                        }
                                    """)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "status": 403,
                                            "code": "S007",
                                            "message": "카테고리에 대한 접근권한이 없습니다.",
                                            "timestamp": "2025-10-16T19:34:19.8703"
                                        }
                                    """)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "code": "S008",
                                            "message": "이미 존재하는 카테고리입니다.",
                                            "timestamp": "2025-10-16T19:34:19.8703"
                                        }
                                    """)
            )
        )
    })
    public ResponseEntity<CategoryResponseDto> createCategory(
        @Valid @RequestBody CategoryCreateRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );




    @Operation(summary = "카테고리 수정", description = "관리자는 카테고리명을 수정합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "id": "80fd1533-a151-11f0-982b-00155d41dd91",
                                            "name": "패스트푸드"
                                        }
                                    """)
            )
        ),
        @ApiResponse(
            responseCode = "403",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "status": 403,
                                            "code": "S007",
                                            "message": "카테고리에 대한 접근권한이 없습니다.",
                                            "timestamp": "2025-10-16T19:34:19.8703"
                                        }
                                    """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "status": 404,
                                            "code": "S006",
                                            "message": "카테고리를 찾을 수 없습니다.",
                                            "timestamp": "2025-10-16T19:34:19.8703"
                                        }
                                    """)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "status": 409,
                                            "code": "S008",
                                            "message": "이미 존재하는 카테고리입니다.",
                                            "timestamp": "2025-10-16T19:34:19.8703"
                                        }
                                    """)
            )
        )
    })
    public ResponseEntity<CategoryResponseDto> updateCategory(
        @PathVariable UUID categoryId,
        @Valid @RequestBody CategoryUpdateRequestDto requestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
