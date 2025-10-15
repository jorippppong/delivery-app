package com.sparta.foodorder.domain.review.application.dto;

import java.util.List;

import org.springframework.data.domain.Page;

public record ReviewPageResponseDto(
	List<ReviewResponseDto> content,
	Long totalElements,
	Integer totalPages,
	Integer currentPage,
	Integer size,
	Double avgRating
) {
	public static ReviewPageResponseDto from(Page<ReviewResponseDto> page) {
		return new ReviewPageResponseDto(
			page.getContent(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.getNumber(),
			page.getSize(),
			null
		);
	}

	public static ReviewPageResponseDto from(Page<ReviewResponseDto> page, Double avgRating) {
		return new ReviewPageResponseDto(
			page.getContent(),
			page.getTotalElements(),
			page.getTotalPages(),
			page.getNumber(),
			page.getSize(),
			avgRating
		);
	}
}