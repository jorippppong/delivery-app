package com.sparta.foodorder.global.dto;

import lombok.Getter;

@Getter
public class ErrorResponse<T> {

    private final String message;
    private final ErrorDetails error;

    private ErrorResponse(String message, ErrorDetails error) {
        this.message = message;
        this.error = error;
    }

    public static <T> ErrorResponse<T> fail(String message, ErrorDetails error) {
        return new ErrorResponse<>(message, error);
    }

    @Getter
    public static class ErrorDetails {

        private final String code;
        private final String details;

        public ErrorDetails(String code, String details) {
            this.code = code;
            this.details = details;
        }
    }
}