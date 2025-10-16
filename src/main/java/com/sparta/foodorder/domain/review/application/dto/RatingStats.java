package com.sparta.foodorder.domain.review.application.dto;

public record RatingStats(
	Long count,
	Double average
) {}