package com.sparta.foodorder.domain.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    CUSTOMER("고객"),
    STORE_OWNER("사장"),
    ADMIN("관리자");

    private final String description;
}
