package com.sparta.foodorder.domain.store.application.dto;

import com.sparta.foodorder.domain.store.domain.Store;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record StoreResponseDto(
    UUID storeId,
    Long ownerId,
    String name,
    String description,
    String address,
    Double longitude,
    Double latitude,
    String phoneNumber,
    Boolean isActive,
    Long ratingCount,
    Double ratingAvg,
    Long deliveryFee,
    Long minOrderAmount,
    LocalTime opensAt,
    LocalTime closesAt,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

    public static StoreResponseDto from(Store store) {
        return StoreResponseDto.builder()
            .storeId(store.getId())
            .ownerId(store.getOwnerId())
            .name(store.getName())
            .description(store.getDescription())
            .address(store.getAddress())
            .longitude(store.getLocation().getX())
            .latitude(store.getLocation().getY())
            .isActive(store.getIsActive())
            .phoneNumber(store.getPhoneNumber())
            .ratingCount(store.getRatingCount())
            .ratingAvg(store.getRatingAvg())
            .deliveryFee(store.getDeliveryFee())
            .minOrderAmount(store.getMinOrderAmount())
            .opensAt(store.getOpensAt())
            .closesAt(store.getClosesAt())
            .createdAt(store.getCreatedAt())
            .updatedAt(store.getUpdatedAt())
            .build();
    }
}
