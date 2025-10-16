package com.sparta.foodorder.domain.review.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.sparta.foodorder.domain.review.application.dto.RatingStats;
import com.sparta.foodorder.domain.review.application.dto.ReviewWithStoreName;
import com.sparta.foodorder.domain.review.domain.Review;
import com.sparta.foodorder.domain.review.domain.ReviewRepository;

@Repository
public class ReviewRepositoryImpl implements ReviewRepository {

	private final ReviewJpaRepository jpaRepository;

	public ReviewRepositoryImpl(ReviewJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public Review save(Review review) {
		return jpaRepository.save(review);
	}

	@Override
	public Optional<Review> findById(UUID id) {
		return jpaRepository.findById(id);
	}

	@Override
	public boolean existsByOrderId(UUID orderId) {
		return jpaRepository.existsByOrderIdAndDeletedAtIsNull(orderId);
	}

	@Override
	public Page<Review> findByStoreId(UUID storeId, Pageable pageable) {
		return jpaRepository.findByStoreIdAndDeletedAtIsNull(storeId, pageable);
	}

	@Override
	public Page<ReviewWithStoreName> findByUserIdWithStoreName(Long userId, Pageable pageable) {
		return jpaRepository.findByUserIdWithStoreName(userId, pageable);
	}

	@Override
	public RatingStats calculateRatingStats(UUID storeId) {
		return jpaRepository.calculateRatingStats(storeId);
	}
}
