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
import com.sparta.foodorder.domain.review.domain.Review;
import com.sparta.foodorder.domain.review.domain.ReviewRepository;
import com.sparta.foodorder.domain.review.domain.ReviewService;
import com.sparta.foodorder.domain.store.domain.Store;
import com.sparta.foodorder.domain.store.domain.StoreRepository;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.global.dto.PagedResponse;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;

@Service
public class ReviewServiceImpl implements ReviewService {

	private final ReviewRepository reviewRepository;
	private final OrderRepository orderRepository;
	private final StoreRepository storeRepository;

	public ReviewServiceImpl(ReviewRepository reviewRepository, OrderRepository orderRepository, StoreRepository storeRepository) {
		this.reviewRepository = reviewRepository;
		this.orderRepository = orderRepository;
		this.storeRepository = storeRepository;
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

		Store store = storeRepository.findById(request.storeId())
			.orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

		Review review = new Review(
			userId,
			request.storeId(),
			request.orderId(),
			request.rating(),
			request.content()
		);

		Review savedReview = reviewRepository.save(review);
		return ReviewResponseDto.from(savedReview);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<ReviewResponseDto> getStoreReviews(UUID storeId, Pageable pageable) {
		storeRepository.findById(storeId)
			.orElseThrow(() -> new BusinessException(ErrorCode.STORE_NOT_FOUND));

		Page<Review> reviewPage = reviewRepository.findByStoreId(storeId, pageable);

		Page<ReviewResponseDto> dtoPage = reviewPage.map(ReviewResponseDto::from);

		return PagedResponse.of(dtoPage);
	}

	@Override
	@Transactional(readOnly = true)
	public PagedResponse<ReviewResponseDto> getMyReviews(Long userId, Pageable pageable) {
		Page<Review> reviewPage = reviewRepository.findByUserId(userId, pageable);

		Page<ReviewResponseDto> dtoPage = reviewPage.map(review -> {
			String storeName = storeRepository.findById(review.getStoreId())
				.map(Store::getName)
				.orElse("삭제된 가게");
			return ReviewResponseDto.from(review, storeName);
		});

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

		review.softDelete(username);
		reviewRepository.save(review);
	}
}
