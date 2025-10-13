package com.sparta.foodorder.domain.store.application.dto;

import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record StoreCreateRequestDto(
    @NotBlank(message = "가게명은 필수입니다.")
    String name,

    String description,

    @NotBlank(message = "주소는 필수입니다.")
    String address,

    @NotNull(message = "위치정보는 필수입니다.")
    Double longitude,

    @NotNull(message = "위치정보는 필수입니다.")
    Double latitude,

    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{3}-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    String phoneNumber,

    @NotNull(message = "최소주문금액은 필수입니다.")
    Long minOrderAmount,

    @NotNull(message = "배달비는 필수입니다.")
    Long deliveryFee,

    @NotNull(message = "영업 시작시간은 필수입니다.")
    LocalTime opensAt,

    @NotNull(message = "영업 종료시간은 필수입니다.")
    LocalTime closesAt,

    @NotEmpty(message = "카테고리 설정은 필수입니다.")
    Set<UUID> categories
) {

}