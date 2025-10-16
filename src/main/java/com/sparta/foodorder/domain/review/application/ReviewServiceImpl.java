package com.sparta.foodorder.domain.review.application;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sparta.foodorder.domain.order.domain.Order;
import com.sparta.foodorder.domain.order.domain.OrderRepository;
import com.sparta.foodorder.domain.order.domain.OrderStatus;
import com.sparta.foodorder.domain.review.application.dto.ReviewCreateRequestDto;
import com.sparta.foodorder.domain.review.application.dto.ReviewResponseDto;
import com.sparta.foodorder.domain.review.application.dto.ReviewWithStoreName;
import com.sparta.foodorder.domain.review.domain.Review;
import com.sparta.foodorder.domain.review.domain.ReviewRepository;
import com.sparta.foodorder.domain.review.domain.ReviewService;
import com.sparta.foodorder.domain.store.domain.StoreService;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.dto.PagedResponse;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;

@Service
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final StoreService storeService;

	public ReviewServiceImpl(ReviewRepository reviewRepository, OrderRepository orderRepository, StoreService storeService) {
		this.reviewRepository = reviewRepository;
		this.orderRepository = orderRepository;
		this.storeService = storeService;
	}

	@Override
	@Transactional
	public ReviewResponseDto createReview(ReviewCreateRequestDto request, Long userId, String username) {
		Order order = orderRepository.findById(request.orderId())
			.orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

		order.validateOrderWriter(userId);

		if (order.getOrderStatus() != OrderStatus.COMPLETE) {
			throw new BusinessException(ErrorCode.ORDER_NOT_COMPLETED);
		}

		if (reviewRepository.existsByOrderId(request.orderId())) {
			throw new BusinessException(ErrorCode.REVIEW_ALREADY_EXISTS);
		}

		storeService.validateExistenceById(request.storeId());

		Review review = new Review(
			userId,
			request.storeId(),
			request.orderId(),
			request.rating(),
			request.content()
		);

		Review savedReview = reviewRepository.save(review);

		storeService.updateRating(request.storeId());

		return ReviewResponseDto.from(savedReview);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<ReviewResponseDto> getStoreReviews(UUID storeId, Pageable pageable) {
		storeService.validateExistenceById(storeId);

		Page<Review> reviewPage = reviewRepository.findByStoreId(storeId, pageable);

		Page<ReviewResponseDto> dtoPage = reviewPage.map(ReviewResponseDto::from);

		return PagedResponse.of(dtoPage);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<ReviewResponseDto> getMyReviews(Long userId, Pageable pageable) {
		Page<ReviewWithStoreName> resultPage = reviewRepository.findByUserIdWithStoreName(userId, pageable);

		Page<ReviewResponseDto> dtoPage = resultPage.map(result ->
			ReviewResponseDto.from(
				result.review(),
				result.storeName() != null ? result.storeName() : "삭제된 가게"
			)
		);

		return PagedResponse.of(dtoPage);
	}

	@Override
	@Transactional
	public void deleteReview(UUID reviewId, Long userId, UserRole role, String username) {
		Review review = reviewRepository.findById(reviewId)
			.orElseThrow(() -> new BusinessException(ErrorCode.REVIEW_NOT_FOUND));

		if (!review.getUserId().equals(userId) && role != UserRole.MANAGER) {
			throw new BusinessException(ErrorCode.REVIEW_ACCESS_DENIED);
		}

		UUID storeId = review.getStoreId();

		review.softDelete(username);

		storeService.updateRating(storeId);

		reviewRepository.save(review);
	}
}
