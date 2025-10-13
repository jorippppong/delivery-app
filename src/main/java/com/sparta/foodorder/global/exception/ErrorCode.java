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
    DEFAULT_ADDRESS_EXISTS(HttpStatus.BAD_REQUEST, "A003", "이미 기본 배송지가 존재합니다"),

    // 결제 관련 에러
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAY001", "결제를 찾을 수 없습니다"),
    PAYMENT_ALREADY_EXISTS(HttpStatus.CONFLICT, "PAY002", "이미 결제가 존재하는 주문입니다"),
    PAYMENT_NOT_REFUNDABLE(HttpStatus.BAD_REQUEST, "PAY003", "환불이 불가능한 결제 상태입니다"),
    PAYMENT_ACCESS_DENIED(HttpStatus.FORBIDDEN, "PAY004", "결제에 대한 접근 권한이 없습니다"),
    PAYMENT_REFUND_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "PAY005", "환불 가능 시간이 초과되었습니다"),
    PAYMENT_ALREADY_REFUNDED(HttpStatus.BAD_REQUEST, "PAY006", "이미 환불된 결제입니다"),
    PAYMENT_USER_MISMATCH(HttpStatus.FORBIDDEN, "PAY007", "본인의 결제만 처리할 수 있습니다"),
    PAYMENT_OWNER_MISMATCH(HttpStatus.FORBIDDEN, "PAY008", "본인 가게의 주문만 처리할 수 있습니다"),

    // AI 관련 에러
    AI_PROVIDER_NOT_FOUND(HttpStatus.BAD_REQUEST, "AI001", "지원하지 않는 AI Provider입니다"),
    AI_PROMPT_REQUIRED(HttpStatus.BAD_REQUEST, "AI002", "프롬프트는 필수입니다"),
    AI_MODEL_NOT_FOUND(HttpStatus.BAD_REQUEST, "AI004", "지원하지 않는 AI 모델입니다"),
    AI_EMPTY_RESPONSE(HttpStatus.INTERNAL_SERVER_ERROR, "A201", "AI가 빈 응답을 반환했습니다"),
    AI_RESPONSE_PARSE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "A202", "AI 응답 파싱에 실패했습니다"),
    AI_GEMINI_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "A301", "Gemini API 오류가 발생했습니다"),

    // 가게 관련 에러
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "S001", "가게를 찾을 수 없습니다."),
    STORE_ALREADY_EXIST(HttpStatus.CONFLICT, "S002", "이미 존재하는 가게입니다."),
    OWNER_ALREADY_HAS_STORE(HttpStatus.CONFLICT, "S003", "한 명의 사장님은 하나의 가게만 등록 가능합니다."),
    PHONE_ALREADY_EXIST(HttpStatus.CONFLICT, "S004", "이미 존재하는 전화번호입니다."),
    STORE_PERMISSION_DENIED(HttpStatus.FORBIDDEN, "S005", "가게에 대한 접근권한이 없습니다."),
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "S006", "카테고리를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
