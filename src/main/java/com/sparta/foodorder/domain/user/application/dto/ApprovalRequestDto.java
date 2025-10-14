package com.sparta.foodorder.domain.user.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ApprovalRequestDto {

    @NotNull(message = "승인 여부는 필수입니다")
    private Boolean approved;  

    @Size(max = 500, message = "사유는 500자 이하여야 합니다")
    private String reason;  
}

