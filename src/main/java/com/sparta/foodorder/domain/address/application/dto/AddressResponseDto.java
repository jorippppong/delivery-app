package com.sparta.foodorder.domain.address.application.dto;

import com.sparta.foodorder.domain.address.domain.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressResponseDto {

    private Long id;
    private Long userId;
    private String userEmail;
    private String addressName;
    private String addressLine;
    private String detailAddress;
    private String postalCode;
    private String recipientName;
    private String recipientPhone;
    private Boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AddressResponseDto from(Address address) {
        return AddressResponseDto.builder()
                .id(address.getId())
                .userId(address.getUser().getId())
                .userEmail(address.getUser().getUserEmail())
                .addressName(address.getAddressName())
                .addressLine(address.getAddressLine())
                .detailAddress(address.getDetailAddress())
                .postalCode(address.getPostalCode())
                .recipientName(address.getRecipientName())
                .recipientPhone(address.getRecipientPhone())
                .isDefault(address.getIsDefault())
                .createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }
}

