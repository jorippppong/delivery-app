package com.sparta.foodorder.domain.user.application.dto;

import com.sparta.foodorder.domain.user.domain.UserStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용자 상태 변경 요청")
public class UserStatusUpdateRequestDto {

    @NotNull(message = "사용자 상태는 필수입니다")
    @Schema(description = "사용자 상태", example = "ACTIVE", allowableValues = {"ACTIVE", "BANNED", "WITHDRAWN"})
    private UserStatus status;

    @Schema(description = "상태 변경 사유", example = "부적절한 행동")
    private String reason;
}

