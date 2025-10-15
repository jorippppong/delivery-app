package com.sparta.foodorder.domain.review.presentation;

import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.review.application.dto.ReviewCreateRequestDto;
import com.sparta.foodorder.domain.review.application.dto.ReviewPageResponseDto;
import com.sparta.foodorder.domain.review.application.dto.ReviewResponseDto;
import com.sparta.foodorder.domain.review.domain.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/v1/reviews")
public class ReviewController {

	private final ReviewService reviewService;

	public ReviewController(ReviewService reviewService) {
		this.reviewService = reviewService;
	}

	@Operation(summary = "리뷰 작성", description = "주문 완료 후 가게에 대한 리뷰를 작성합니다")
	@PreAuthorize("hasRole('USER')")
	@PostMapping
	public ResponseEntity<ReviewResponseDto> createReview(
		@Valid @RequestBody ReviewCreateRequestDto request,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		ReviewResponseDto response = reviewService.createReview(
			request,
			userDetails.getUserId(),
			userDetails.getUsername()
		);
		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@Operation(summary = "가게 리뷰 조회", description = "특정 가게의 리뷰 목록을 조회합니다")
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/stores/{storeId}")
	public ResponseEntity<ReviewPageResponseDto> getStoreReviews(
		@PathVariable UUID storeId,
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sort,
		@RequestParam(defaultValue = "desc") String direction
	) {
		if (size != 10 && size != 30 && size != 50) {
			size = 10;
		}

		Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
			? Sort.Direction.ASC
			: Sort.Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

		ReviewPageResponseDto response = reviewService.getStoreReviews(storeId, pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "내 리뷰 조회", description = "로그인한 사용자가 작성한 리뷰 목록을 조회합니다")
	@PreAuthorize("hasRole('USER')")
	@GetMapping("/my")
	public ResponseEntity<ReviewPageResponseDto> getMyReviews(
		@RequestParam(defaultValue = "0") int page,
		@RequestParam(defaultValue = "10") int size,
		@RequestParam(defaultValue = "createdAt") String sort,
		@RequestParam(defaultValue = "desc") String direction,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		if (size != 10 && size != 30 && size != 50) {
			size = 10;
		}

		Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
			? Sort.Direction.ASC
			: Sort.Direction.DESC;
		Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

		ReviewPageResponseDto response = reviewService.getMyReviews(userDetails.getUserId(), pageable);
		return ResponseEntity.ok(response);
	}

	@Operation(summary = "리뷰 삭제", description = "작성한 리뷰를 삭제합니다")
	@PreAuthorize("hasAnyRole('USER', 'MANAGER', 'ADMIN')")
	@DeleteMapping("/{reviewId}")
	public ResponseEntity<Void> deleteReview(
		@PathVariable UUID reviewId,
		@AuthenticationPrincipal CustomUserDetails userDetails
	) {
		reviewService.deleteReview(
			reviewId,
			userDetails.getUserId(),
			userDetails.getRole(),
			userDetails.getUsername()
		);
		return ResponseEntity.ok().build();
	}
}