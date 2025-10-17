package com.sparta.foodorder.domain.store.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;

@Schema(description = "가게 생성 요청 DTO")
public record StoreCreateRequestDto(
    @Schema(description = "가게명", example = "최가네버섯샤브매운탕칼국수")
    @NotBlank(message = "가게명은 필수입니다.")
    String name,

    @Schema(description = "가게 설명", example = "가성비 최고의 얼큰한 샤브샤브 세트")
    @NotBlank(message = "가게설명은 필수입니다.")
    String description,

    @Schema(description = "주소", example = "서울 강남구 도산대로51길 36 2층3층")
    @NotBlank(message = "주소는 필수입니다.")
    String address,

    @Schema(description = "위치정보(경도)", example = "127.0276")
    @NotNull(message = "위치정보는 필수입니다.")
    Double longitude,

    @Schema(description = "위치정보(위도)", example = "37.4979")
    @NotNull(message = "위치정보는 필수입니다.")
    Double latitude,

    @Schema(description = "전화번호", example = "010-0000-0000")
    @NotBlank(message = "전화번호는 필수입니다.")
    @Pattern(regexp = "^[0-9]{3}-[0-9]{3,4}-[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    String phoneNumber,

    @Schema(description = "최소주문금액", example = "15000")
    @NotNull(message = "최소주문금액은 필수입니다.")
    Long minOrderAmount,

    @Schema(description = "배달비", example = "4000")
    @NotNull(message = "배달비는 필수입니다.")
    Long deliveryFee,

    @Schema(description = "영업 시작시간", example = "11:00")
    @NotNull(message = "영업 시작시간은 필수입니다.")
    LocalTime opensAt,

    @Schema(description = "영업 종료시간", example = "21:40")
    @NotNull(message = "영업 종료시간은 필수입니다.")
    LocalTime closesAt,

    @Schema(description = "카테고리 ID 리스트", example = "[\"80fcdd23-a151-11f0-982b-00155d41dd91\", \"80fd081b-a151-11f0-982b-00155d41dd91\"]")
    @NotEmpty(message = "카테고리 설정은 필수입니다.")
    Set<UUID> categories
) {

}