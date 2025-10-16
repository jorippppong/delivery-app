package com.sparta.foodorder.domain.review.domain;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.sparta.foodorder.domain.review.application.dto.ReviewCreateRequestDto;
import com.sparta.foodorder.domain.review.application.dto.ReviewResponseDto;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.dto.PagedResponse;

public interface ReviewService {

	ReviewResponseDto createReview(ReviewCreateRequestDto request, Long userId, String username);

	PagedResponse<ReviewResponseDto> getStoreReviews(UUID storeId, Pageable pageable);

	PagedResponse<ReviewResponseDto> getMyReviews(Long userId, Pageable pageable);

	void deleteReview(UUID reviewId, Long userId, UserRole role, String username);
}
