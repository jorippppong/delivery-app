package com.sparta.foodorder.domain.order.presentation;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.order.presentation.dto.*;
import com.sparta.foodorder.global.dto.PagedResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

@Tag(name = "주문", description = "주문 관련 API")
public interface OrderApiDocs {
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    @ApiResponse(
            responseCode = "200",
            content = @Content(
                    mediaType = "application/json",
                    examples = @ExampleObject(value = """
                            {
                              "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479"
                            }
                            """)
            )
    )
    ResponseEntity<CreateOrderResponseDto> createOrder(
            CreateOrderRequestDto dto,
            CustomUserDetails userDetails
    );


    @Operation(summary = "주문 취소", description = "주문을 취소합니다. (created, pending  -> canceled)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "code": "O001",
                                          "message": "주문을 찾을 수 없습니다.",
                                          "timestamp": "2025-10-16T12:51:00"
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
                                          "code": "O003",
                                          "message": "주문에 접근할 수 없습니다.(주문의 작성자, 가게의 주인만 접근 가능)",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "code": "O004",
                                          "message": "created, pending 상태일 때만 주문 취소 가능합니다.",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<Void> cancelOrder(
            UUID orderId,
            CustomUserDetails userDetails
    );


    @Operation(summary = "주문 수락", description = "사장님이 주문을 수락합니다. (pending -> accept)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "code": "O001",
                                          "message": "주문을 찾을 수 없습니다.",
                                          "timestamp": "2025-10-16T12:51:00"
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
                                          "code": "O003",
                                          "message": "주문에 접근할 수 없습니다.(주문의 작성자, 가게의 주인만 접근 가능)",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "code": "O005",
                                          "message": "pending 상태일 때만 주문 수락 가능합니다.",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<Void> acceptOrder(
            UUID orderId,
            CustomUserDetails userDetails
    );


    @Operation(summary = "주문 거절", description = "사장님이 주문을 거절합니다. (pending -> reject)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "code": "O001",
                                          "message": "주문을 찾을 수 없습니다.",
                                          "timestamp": "2025-10-16T12:51:00"
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
                                          "code": "O003",
                                          "message": "주문에 접근할 수 없습니다.(주문의 작성자, 가게의 주인만 접근 가능)",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "code": "O006",
                                          "message": "pending 상태일 때만 주문 거절 가능합니다.",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<Void> rejectOrder(
            UUID orderId,
            CustomUserDetails userDetails
    );


    @Operation(summary = "배송 준비", description = "음식 조리가 완료되어서 배송 라이더 님의 픽업을 기다립니다. (accept -> ready)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "code": "O001",
                                          "message": "주문을 찾을 수 없습니다.",
                                          "timestamp": "2025-10-16T12:51:00"
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
                                          "code": "O003",
                                          "message": "주문에 접근할 수 없습니다.(주문의 작성자, 가게의 주인만 접근 가능)",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "code": "O007",
                                          "message": "accept 상태일 때만 주문 취소 가능합니다.",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<Void> readyOrder(
            UUID orderId,
            CustomUserDetails userDetails
    );


    @Operation(summary = "배달 중", description = "라이더 님이 배달 중입니다. (ready -> delivering)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "code": "O001",
                                          "message": "주문을 찾을 수 없습니다.",
                                          "timestamp": "2025-10-16T12:51:00"
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
                                          "code": "O003",
                                          "message": "주문에 접근할 수 없습니다.(주문의 작성자, 가게의 주인만 접근 가능)",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "code": "O008",
                                          "message": "ready 상태일 때만 주문 취소 가능합니다.",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<Void> deliverOrder(
            UUID orderId,
            CustomUserDetails userDetails
    );

    @Operation(summary = "음식 배송지에서 수락 완료", description = "주문이 배송까지 끝나서 주문자에게 도착했습니다. (delivering -> complete)")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200"
            ),
            @ApiResponse(
                    responseCode = "404",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "code": "O001",
                                          "message": "주문을 찾을 수 없습니다.",
                                          "timestamp": "2025-10-16T12:51:00"
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
                                          "code": "O003",
                                          "message": "주문에 접근할 수 없습니다.(주문의 작성자, 가게의 주인만 접근 가능)",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 400,
                                          "code": "O009",
                                          "message": "delivering 상태일 때만 주문 취소 가능합니다.",
                                          "timestamp": "2025-10-16T12:52:00"
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<Void> completeOrder(
            UUID orderId,
            CustomUserDetails userDetails
    );


    @Operation(summary = "유저 주문 조회", description = "로그인한 유저의 주문 내역을 페이징 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "data": [
                                            {
                                              "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                                              "storeId": "a3f5c10b-12ab-4cde-89ef-0a1b2c3d4e5f",
                                              "storeName": "맛있는 버거집",
                                              "menus": [
                                                {
                                                  "menuId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
                                                  "menuName": "치즈버거 세트",
                                                  "quantity": 2,
                                                  "options": [
                                                    {
                                                      "optionName": "사이드",
                                                      "optionValues": ["감자튀김", "콜라"]
                                                    },
                                                    {
                                                      "optionName": "토핑",
                                                      "optionValues": ["치즈", "베이컨"]
                                                    }
                                                  ]
                                                }
                                              ],
                                              "status": "CREATED",
                                              "totalPrice": 25000,
                                              "createdAt": "2025-10-16T13:05:00"
                                            }
                                          ],
                                          "page": 1,
                                          "size": 10,
                                          "hasNext": true
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<PagedResponse<GetUserOrdersResponseDto>> getUserOrders(
            int page,
            int size,
            CustomUserDetails userDetails
    );

    @Operation(summary = "가게 주문 조회", description = "가게 ID로 주문 내역을 페이징 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "data": [
                                            {
                                              "orderId": "f47ac10b-58cc-4372-a567-0e02b2c3d479",
                                              "customerNickname": "홍길동",
                                              "addressLine": "서울시 강남구 테헤란로 123",
                                              "detailAddress": "101호",
                                              "menus": [
                                                {
                                                  "menuId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
                                                  "menuName": "치즈버거 세트",
                                                  "quantity": 2,
                                                  "options": [
                                                    {
                                                      "optionName": "사이드",
                                                      "optionValues": ["감자튀김", "콜라"]
                                                    },
                                                    {
                                                      "optionName": "토핑",
                                                      "optionValues": ["치즈", "베이컨"]
                                                    }
                                                  ]
                                                }
                                              ],
                                              "status": "CREATED",
                                              "totalAmount": 25000,
                                              "createdAt": "2025-10-16T13:05:00"
                                            }
                                          ],
                                          "page": 1,
                                          "size": 10,
                                          "hasNext": true
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<PagedResponse<GetStoreOrdersResponseDto>> getStoreOrders(
            UUID storeId,
            int page,
            int size,
            CustomUserDetails userDetails
    );


    @Operation(summary = "주문 조회", description = "주문 ID로 주문 정보를 조회합니다.")
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "주문 조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "customerNickname": "홍길동",
                                          "addressLine": "서울시 강남구 테헤란로 123",
                                          "detailAddress": "101호",
                                          "menus": [
                                            {
                                              "menuId": "d290f1ee-6c54-4b01-90e6-d701748f0851",
                                              "menuName": "치즈버거 세트",
                                              "quantity": 2,
                                              "options": [
                                                {
                                                  "optionName": "사이드",
                                                  "optionValues": ["감자튀김", "콜라"]
                                                },
                                                {
                                                  "optionName": "토핑",
                                                  "optionValues": ["치즈", "베이컨"]
                                                }
                                              ]
                                            }
                                          ],
                                          "status": "CREATED",
                                          "totalAmount": 25000,
                                          "createdAt": "2025-10-16T13:05:00"
                                        }
                                    """)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "주문을 찾을 수 없음",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(value = """
                                        {
                                          "status": 404,
                                          "code": "O001",
                                          "message": "주문을 찾을 수 없습니다.",
                                          "timestamp": "2025-10-16T12:51:00"
                                        }
                                    """)
                    )
            )
    })
    ResponseEntity<GetOrderResponseDto> getOrder(
            UUID orderId,
            CustomUserDetails userDetails
    );
}
