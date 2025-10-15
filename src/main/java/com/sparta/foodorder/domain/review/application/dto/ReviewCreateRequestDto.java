package com.sparta.foodorder.domain.review.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ReviewCreateRequestDto(
	@NotNull(message = "가게 ID는 필수입니다.")
	UUID storeId,

	@NotNull(message = "주문 ID는 필수입니다.")
	UUID orderId,

	@NotNull(message = "평점은 필수입니다.")
	@Min(value = 1, message = "평점은 최소 1점입니다.")
	@Max(value = 5, message = "평점은 최대 5점입니다.")
	Integer rating,

	@Size(max = 1000, message = "리뷰 내용은 최대 1000자까지 작성 가능합니다.")
	String content
) {}