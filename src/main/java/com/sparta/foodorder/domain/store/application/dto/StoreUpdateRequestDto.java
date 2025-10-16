package com.sparta.foodorder.domain.store.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

public record StoreUpdateRequestDto(
    @Schema(description = "가게명", example = "최가네버섯샤브매운탕칼국수")
    String name,

    @Schema(description = "가게 설명", example = "가성비 최고의 얼큰한 샤브샤브 세트")
    String description,

    @Schema(description = "주소", example = "서울 강남구 도산대로51길 36 2층3층")
    String address,

    @Schema(description = "위치정보(경도)", example = "127.0276")
    Double longitude,

    @Schema(description = "위치정보(위도)", example = "37.4979")
    Double latitude,

    @Schema(description = "전화번호", example = "010-0000-0000")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다.")
    String phoneNumber,

    @Schema(description = "가게운영여부", examples = "true")
    Boolean isActive,

    @Schema(description = "최소주문금액", example = "15000")
    Long minOrderAmount,

    @Schema(description = "배달비", example = "4000")
    Long deliveryFee,

    @Schema(description = "영업 시작시간", example = "11:00")
    LocalTime opensAt,

    @Schema(description = "영업 종료시간", example = "21:40")
    LocalTime closesAt,

    @Schema(description = "카테고리 ID 리스트", example = "[\"80fcdd23-a151-11f0-982b-00155d41dd91\", \"80fd081b-a151-11f0-982b-00155d41dd91\"]")
    Set<UUID> categories
) {

}
