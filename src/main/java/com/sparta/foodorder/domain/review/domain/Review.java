package com.sparta.foodorder.domain.review.domain;

import java.util.UUID;

import com.sparta.foodorder.global.common.BaseDeleteEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_review")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseDeleteEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	@Column(columnDefinition = "BINARY(16)", nullable = false)
	private UUID id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "store_id", nullable = false)
	private UUID storeId;

	@Column(name = "order_id", nullable = false, unique = true)
	private UUID orderId;

	@Column(name = "rating", nullable = false)
	private Integer rating;

	@Column(name = "content", columnDefinition = "TEXT")
	private String content;

	public Review(Long userId, UUID storeId, UUID orderId, Integer rating, String content) {
		this.userId = userId;
		this.storeId = storeId;
		this.orderId = orderId;
		this.rating = rating;
		this.content = content;
	}
}
