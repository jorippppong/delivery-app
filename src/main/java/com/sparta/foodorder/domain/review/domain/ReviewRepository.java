package com.sparta.foodorder.domain.review.domain;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.sparta.foodorder.domain.review.application.dto.ReviewWithStoreName;

public interface ReviewRepository {

	Review save(Review review);

	Optional<Review> findById(UUID id);

	boolean existsByOrderId(UUID orderId);

	Page<Review> findByStoreId(UUID storeId, Pageable pageable);

	Page<ReviewWithStoreName> findByUserIdWithStoreName(Long userId, Pageable pageable);
}
