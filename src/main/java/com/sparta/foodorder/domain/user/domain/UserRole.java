package com.sparta.foodorder.domain.user.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserRole {
    USER("사용자"),
    OWNER("오너"),
    ADMIN("관리자"),
    MANAGER("매니저");

    private final String description;
}
