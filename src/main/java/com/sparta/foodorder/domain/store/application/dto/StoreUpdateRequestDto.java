package com.sparta.foodorder.domain.store.application.dto;

import jakarta.validation.constraints.Pattern;
import java.time.LocalTime;
import java.util.Set;
import java.util.UUID;
import org.locationtech.jts.geom.Point;

public record StoreUpdateRequestDto(
    String name,

    String description,

    String address,

    Point location,

    @Pattern(regexp = "^[0-9]{3}-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    String phoneNumber,

    Boolean isActive,

    Long minOrderAmount,

    Long deliveryFee,

    LocalTime opensAt,

    LocalTime closesAt,

    Set<UUID> categories
) {

}
