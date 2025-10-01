package com.sparta.foodorder.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 비즈니스 예외 처리
    @ExceptionHandler(BusinessException.class)
    protected ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
        log.error("BusinessException: code={}, message={}", ex.getErrorCode().getCode(), ex.getMessage());
        ErrorCode errorCode = ex.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode, ex.getMessage());
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // Validation 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex) {
        log.error("MethodArgumentNotValidException: {}", ex.getMessage());
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.INVALID_INPUT_VALUE,
                ErrorResponse.FieldError.of(ex.getBindingResult()));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // 모든 예외 처리
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(Exception ex) {
        log.error("Unhandled Exception: ", ex);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
