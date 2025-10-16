package com.sparta.foodorder.domain.review.infrastructure;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.sparta.foodorder.domain.review.application.dto.RatingStats;
import com.sparta.foodorder.domain.review.application.dto.ReviewWithStoreName;
import com.sparta.foodorder.domain.review.domain.Review;

public interface ReviewJpaRepository extends JpaRepository<Review, UUID> {

	boolean existsByOrderIdAndDeletedAtIsNull(UUID orderId);

	Page<Review> findByStoreIdAndDeletedAtIsNull(UUID storeId, Pageable pageable);

	@Query("""
		SELECT new com.sparta.foodorder.domain.review.application.dto.ReviewWithStoreName(r, s.name)
		FROM Review r
		LEFT JOIN Store s ON r.storeId = s.id
		WHERE r.userId = :userId 
		AND r.deletedAt IS NULL
		ORDER BY r.createdAt DESC
		""")
	Page<ReviewWithStoreName> findByUserIdWithStoreName(@Param("userId") Long userId, Pageable pageable);

	@Query("SELECT new com.sparta.foodorder.domain.review.application.dto.RatingStats(" +
		"COUNT(r), CAST(COALESCE(AVG(CAST(r.rating AS double)), 0.0) AS double)) " +
		"FROM Review r WHERE r.storeId = :storeId AND r.deletedAt IS NULL")
	RatingStats calculateRatingStats(@Param("storeId") UUID storeId);
}