package com.sparta.foodorder.domain.review.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.sparta.foodorder.domain.review.domain.Review;

public record ReviewResponseDto(
	UUID id,
	Long userId,
	UUID storeId,
	String storeName,
	UUID orderId,
	Integer rating,
	String content,
	LocalDateTime createdAt
) {
	public static ReviewResponseDto from(Review review) {
		return new ReviewResponseDto(
			review.getId(),
			review.getUserId(),
			review.getStoreId(),
			null,
			review.getOrderId(),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt()
		);
	}

	public static ReviewResponseDto from(Review review, String storeName) {
		return new ReviewResponseDto(
			review.getId(),
			review.getUserId(),
			review.getStoreId(),
			storeName,
			review.getOrderId(),
			review.getRating(),
			review.getContent(),
			review.getCreatedAt()
		);
	}
}