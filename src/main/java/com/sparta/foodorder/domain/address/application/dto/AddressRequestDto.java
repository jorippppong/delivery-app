package com.sparta.foodorder.domain.address.application.dto;

import com.sparta.foodorder.domain.address.domain.Address;
import com.sparta.foodorder.domain.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {

    @Size(max = 50, message = "주소 이름은 50자 이하여야 합니다")
    private String addressName;

    @NotBlank(message = "주소는 필수입니다")
    @Size(max = 255, message = "주소는 255자 이하여야 합니다")
    private String addressLine;

    @Size(max = 255, message = "상세 주소는 255자 이하여야 합니다")
    private String detailAddress;

    @Size(max = 20, message = "우편번호는 20자 이하여야 합니다")
    private String postalCode;

    @Size(max = 50, message = "수령인 이름은 50자 이하여야 합니다")
    private String recipientName;

    @Size(max = 20, message = "수령인 전화번호는 20자 이하여야 합니다")
    private String recipientPhone;

    private Boolean isDefault;

    public Address toEntity(User user) {
        return Address.builder()
                .user(user)
                .addressName(addressName)
                .addressLine(addressLine)
                .detailAddress(detailAddress)
                .postalCode(postalCode)
                .recipientName(recipientName)
                .recipientPhone(recipientPhone)
                .isDefault(isDefault != null ? isDefault : false)
                .isDeleted(false)  
                .build();
    }
}

