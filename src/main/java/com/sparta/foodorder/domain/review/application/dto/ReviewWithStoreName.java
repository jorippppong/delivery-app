package com.sparta.foodorder.domain.review.application.dto;

import com.sparta.foodorder.domain.review.domain.Review;

public record ReviewWithStoreName(
	Review review,
	String storeName
) {}