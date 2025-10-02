package com.sparta.foodorder.domain.auth.domain;

import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;

public class AuthException extends BusinessException {
    
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
    
    public AuthException(ErrorCode errorCode, String message) {
        super(errorCode, message);
    }
}
