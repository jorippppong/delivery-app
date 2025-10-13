package com.sparta.foodorder.domain.address.application.dto;

import com.sparta.foodorder.domain.address.domain.Address;
import com.sparta.foodorder.domain.user.domain.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    @Size(min = 5, max = 255, message = "주소는 5자 이상 255자 이하여야 합니다")
    private String addressLine;

    @Size(max = 255, message = "상세 주소는 255자 이하여야 합니다")
    private String detailAddress;

    @NotBlank(message = "우편번호는 필수입니다")
    @Pattern(regexp = "^\\d{5}$", message = "우편번호는 5자리 숫자여야 합니다")
    private String postalCode;

    @NotBlank(message = "수령인 이름은 필수입니다")
    @Size(min = 2, max = 50, message = "수령인 이름은 2자 이상 50자 이하여야 합니다")
    private String recipientName;

    @NotBlank(message = "수령인 전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
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

