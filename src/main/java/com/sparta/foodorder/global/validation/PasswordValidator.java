package com.sparta.foodorder.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 비밀번호 정책 검증기
 * - 최소 8자 이상
 * - 영문, 숫자, 특수문자 조합
 */
// ConstraintValidator = 검증하는 일을 하는 인터페이스
// <ValidPassword, String> = ValidPassword 어노테이션을 사용해서 String 타입을 검증한다는 뜻
public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    
    // 정규식 = 글자 패턴을 찾는 공식 (마치 수학 공식 같음!)
    // 이 공식은 "영문자 + 숫자 + 특수문자가 모두 들어있고, 8자 이상"인지 확인함
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
        "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$"
    );
    
    // 실제로 비밀번호를 검증하는 메서드
    // password = 검증할 비밀번호
    // context = 검증할 때 필요한 추가 정보 (지금은 사용 안 함)
    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        // 비밀번호가 아예 없으면 안 됨 (null 체크)
        if (password == null) {
            return false;  // false = 검증 실패
        }
        
        // 정규식 패턴과 맞는지 확인
        // matches() = 패턴과 정확히 일치하는지 확인하는 메서드
        return PASSWORD_PATTERN.matcher(password).matches();
    }
}

