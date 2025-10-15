package com.sparta.foodorder.domain.review.infrastructure;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.foodorder.domain.review.domain.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {

	boolean existsByOrderIdAndDeletedAtIsNull(UUID orderId);

	Page<Review> findByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

	Page<Review> findByUserIdAndDeletedAtIsNull(Long userId, Pageable pageable);

	@Query("SELECT AVG(r.rating) FROM Review r WHERE r.storeId = :storeId AND r.deletedAt IS NULL")
	Double calculateAvgRatingByStoreId(@Param("storeId") UUID storeId);
}