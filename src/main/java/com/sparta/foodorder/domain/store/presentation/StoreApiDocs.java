package com.sparta.foodorder.domain.store.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.store.application.dto.StoreCreateRequestDto;
import com.sparta.foodorder.domain.store.application.dto.StoreDetailResponseDto;
import com.sparta.foodorder.domain.store.application.dto.StoreResponseDto;
import com.sparta.foodorder.domain.store.application.dto.StoreUpdateRequestDto;
import com.sparta.foodorder.global.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "가게", description = "가게 관련 API")
public interface StoreApiDocs {

    @Operation(summary = "가게 생성", description = "새로운 가게를 등록합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                            {
                                                "storeId": "4bf54044-4ecb-4b98-b692-a2f6f70a5a7d",
                                                "ownerId": 1,
                                                "name": "최가네버섯샤브매운탕칼국수",
                                                "description": "가성비 최고의 얼큰한 샤브샤브 세트",
                                                "address": "서울 강남구 도산대로51길 36 2층3층",
                                                "longitude": 127.0276,
                                                "latitude": 37.4979,
                                                "phoneNumber": "010-0000-0000",
                                                "isActive": true,
                                                "ratingCount": null,
                                                "ratingAvg": null,
                                                "deliveryFee": 4000,
                                                "minOrderAmount": 15000,
                                                "opensAt": "11:00:00",
                                                "closesAt": "21:40:00",
                                                "createdAt": "2025-10-16T00:18:41.118404",
                                                "updatedAt": "2025-10-16T00:18:41.118404"
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
                                                    "code": "S005",
                                                    "message": "가게에 대한 접근권한이 없습니다.",
                                                    "timestamp": "2025-10-16T18:01:06.5396248"
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
                                                      "code": "S002",
                                                      "message": "이미 존재하는 가게입니다.",
                                                      "timestamp": "2025-10-16T17:16:27.8618821"
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
                                                    "timestamp": "2025-10-16T17:16:27.8618821"
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
                                                    "code": "S003",
                                                    "message": "한 명의 사장님은 하나의 가게만 등록 가능합니다.",
                                                    "timestamp": "2025-10-16T17:16:27.8618821"
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
                                                    "code": "S004",
                                                    "message": "이미 존재하는 전화번호입니다.",
                                                    "timestamp": "2025-10-16T17:16:27.8618821"
                                                }
                                            """)
                )
            )
    })
    default ResponseEntity<StoreResponseDto> createStore(
        @Valid @RequestBody StoreCreateRequestDto storeCreateRequestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return null;
    }



    @Operation(summary = "가게 수정", description = "가게 정보를 업데이트합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
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
                                            "code": "S005",
                                            "message": "가게에 대한 접근권한이 없습니다.",
                                            "timestamp": "2025-10-16T18:01:06.5396248"
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
                                            "code": "S001",
                                            "message": "가게를 찾을 수 없습니다.",
                                            "timestamp": "2025-10-16T17:55:22.6353731"
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
    ResponseEntity<StoreResponseDto> updateStore(
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreUpdateRequestDto storeUpdateRequestDto,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );




    @Operation(summary = "가게 삭제", description = "가게를 삭제합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200"
        ),
        @ApiResponse(
            responseCode = "403",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(value = """
                                        {
                                            "status": 403,
                                            "code": "S005",
                                            "message": "가게에 대한 접근권한이 없습니다.",
                                            "timestamp": "2025-10-16T18:01:06.5396248"
                                        }
                                    """)
            )
        )
    })
    ResponseEntity<Void> deleteStore(
        @PathVariable UUID storeId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );




    @Operation(
        summary = "가게목록 조회",
        description = "가게 목록을 조회합니다. 파라미터가 있으면 해당 키워드로 검색된 가게 목록을 조회합니다. (생성일자 내림차순으로 정렬)"
    )
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
                                                    "storeId": "46cb334d-a84d-11f0-b0ef-005056c00001",
                                                    "ownerId": 7,
                                                    "name": "스파르타 치킨",
                                                    "description": "맛있는 치킨집입니다.",
                                                    "address": "서울시 강남구 1길 10",
                                                    "longitude": 127.0276,
                                                    "latitude": 37.4979,
                                                    "phoneNumber": "010-1111-0001",
                                                    "isActive": true,
                                                    "ratingCount": 10,
                                                    "ratingAvg": 4.5,
                                                    "deliveryFee": 2000,
                                                    "minOrderAmount": 15000,
                                                    "opensAt": "10:00:00",
                                                    "closesAt": "22:00:00",
                                                    "createdAt": "2025-10-14T00:57:10",
                                                    "updatedAt": null
                                                },
                                                {
                                                    "storeId": "46cded1d-a84d-11f0-b0ef-005056c00001",
                                                    "ownerId": 3,
                                                    "name": "스파르타 피자",
                                                    "description": "얇은 도우와 치즈 듬뿍",
                                                    "address": "서울시 강남구 3길 12",
                                                    "longitude": 127.0285,
                                                    "latitude": 37.4985,
                                                    "phoneNumber": "010-1111-0003",
                                                    "isActive": true,
                                                    "ratingCount": 15,
                                                    "ratingAvg": 4.7,
                                                    "deliveryFee": 2500,
                                                    "minOrderAmount": 18000,
                                                    "opensAt": "10:00:00",
                                                    "closesAt": "22:00:00",
                                                    "createdAt": "2025-10-14T00:57:10",
                                                    "updatedAt": null
                                                }
                                            ],
                                            "page": 0,
                                            "size": 10,
                                            "hasNext": false
                                        }
                                    """)
            )
        )
    })
    ResponseEntity<PagedResponse<StoreResponseDto>> getStores(
        @Parameter(description = "검색키워드", required = false)
        @RequestParam(name = "q", required = false)
        String query,

        @ParameterObject
        @PageableDefault(size = 10)
        @SortDefault(sort = "createdAt", direction = Direction.DESC)
        Pageable pageable,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );




    @Operation(summary = "가게 상세조회", description = "특정 가게를 상세조회합니다.")
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
                    "storeId": "4bf54044-4ecb-4b98-b692-a2f6f70a5a7d",
                    "ownerId": 1,
                    "categories": [
                        {
                            "id": "80fcdd23-a151-11f0-982b-00155d41dd91",
                            "name": "한식"
                        },
                        {
                            "id": "80fd15b3-a151-11f0-982b-00155d41dd91",
                            "name": "탕"
                        }
                    ],
                    "menus": [
                        {
                            "id": "6a3d326e-21f2-4806-8f3e-64137dc1c79c",
                            "name": "스페셜 샤브샤브",
                            "description": "최소주문 2인부터 가능합니다.",
                            "price": 38000,
                            "hidden": false,
                            "active": true,
                            "storeId": "4bf54044-4ecb-4b98-b692-a2f6f70a5a7d",
                            "options": [
                                {
                                    "id": "b62fb884-ce0a-49e3-ab98-c93726639506",
                                    "optionName": "기본육수",
                                    "menuId": "6a3d326e-21f2-4806-8f3e-64137dc1c79c",
                                    "optionValues": []
                                },
                                {
                                    "id": "8a755735-8444-4a03-ab0d-8e7e7ae99811",
                                    "optionName": "매운육수",
                                    "menuId": "6a3d326e-21f2-4806-8f3e-64137dc1c79c",
                                    "optionValues": [
                                        {
                                            "id": "83f943fb-61e2-4128-9dec-fc856bb9b15e",
                                            "value": "1단계",
                                            "description": "보통",
                                            "addPrice": 0
                                        },
                                        {
                                            "id": "aeb6c00e-2752-4569-bd06-05be2230b464",
                                            "value": "2단계",
                                            "description": "매움",
                                           "addPrice": 0
                                        }
                                    ]
                                }
                            ]
                        }
                    ],
                    "name": "최가네버섯샤브매운탕칼국수",
                    "description": "가성비 최고의 얼큰한 샤브샤브 세트",
                    "address": "서울 강남구 도산대로51길 36 2층3층",
                    "longitude": 127.0276,
                    "latitude": 37.4979,
                    "phoneNumber": "010-0000-0000",
                    "isActive": true,
                    "ratingCount": 100,
                    "ratingAvg": 4.7,
                    "deliveryFee": 4000,
                    "minOrderAmount": 15000,
                    "opensAt": "11:00:00",
                    "closesAt": "21:40:00",
                    "createdAt": "2025-10-15T00:18:41.118404",
                    "updatedAt": "2025-10-15T00:18:41.118404"
                }
                """
            )
        )
    )
    ResponseEntity<StoreDetailResponseDto> getStore(
        @PathVariable UUID storeId,
        @AuthenticationPrincipal CustomUserDetails userDetails
    );
}
