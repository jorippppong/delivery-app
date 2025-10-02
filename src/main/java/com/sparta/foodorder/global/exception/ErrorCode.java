package com.sparta.foodorder.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 공통 에러
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다"),
    INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "C002", "잘못된 타입입니다"),
    MISSING_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C003", "필수 입력값이 누락되었습니다"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C004", "서버 오류가 발생했습니다"),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "C005", "허용되지 않은 HTTP 메서드입니다"),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "C006", "접근이 거부되었습니다"),

    // 사용자 관련 에러
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U001", "사용자를 찾을 수 없습니다"),
    DUPLICATE_EMAIL(HttpStatus.CONFLICT, "U002", "이미 존재하는 이메일입니다"),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "U003", "비밀번호가 일치하지 않습니다"),
    UNAUTHORIZED_USER(HttpStatus.UNAUTHORIZED, "U004", "인증되지 않은 사용자입니다"),

    // 상품 관련 에러
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P001", "상품을 찾을 수 없습니다"),
    OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "P002", "재고가 부족합니다"),

    // 주문 관련 에러
    ORDER_NOT_FOUND(HttpStatus.NOT_FOUND, "O001", "주문을 찾을 수 없습니다"),
    INVALID_ORDER_STATUS(HttpStatus.BAD_REQUEST, "O002", "잘못된 주문 상태입니다"),
    ORDER_CANT_ACCESS(HttpStatus.FORBIDDEN, "O003", "주문에 접근할 수 없습니다.(주문의 작성자, 가게의 주인만 접근 가능)"),
    ORDER_CANCEL_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "O004", "created, pending 상태일 때만 주문 취소 가능합니다."),


    // 주소 관련 에러
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "A001", "주소를 찾을 수 없습니다"),
    ADDRESS_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "A002", "해당 주소에 대한 권한이 없습니다"),
    DEFAULT_ADDRESS_EXISTS(HttpStatus.BAD_REQUEST, "A003", "이미 기본 배송지가 존재합니다");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
