package com.sparta.foodorder.domain.user.application.dto;

import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.domain.user.domain.UserStatus;
import com.sparta.foodorder.global.validation.ValidKoreanOrEnglishName;
import com.sparta.foodorder.global.validation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MasterSignupRequestDto {

    @NotBlank(message = "이름은 필수입니다")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다")
    @ValidKoreanOrEnglishName
    private String name;

    @NotBlank(message = "닉네임은 필수입니다")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다")
    private String nickName;

    @NotBlank(message = "이메일은 필수입니다")
    @Email(message = "올바른 이메일 형식이 아닙니다")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다")
    private String userEmail;

    @NotBlank(message = "비밀번호는 필수입니다")
    @ValidPassword
    private String password;

    @NotBlank(message = "전화번호는 필수입니다")
    @Pattern(regexp = "^01[0-9]-?[0-9]{3,4}-?[0-9]{4}$", message = "올바른 전화번호 형식이 아닙니다")
    private String userPhone;

    @Size(max = 500, message = "신청 사유는 500자 이하여야 합니다")
    private String requestReason;  

    public User toEntity(String encodedPassword) {
        return User.builder()
                .name(name)
                .nickName(nickName)
                .userEmail(userEmail)
                .password(encodedPassword)
                .userPhone(userPhone)
                .role(UserRole.MASTER)
                .status(UserStatus.PENDING)  
                .build();
    }
}

