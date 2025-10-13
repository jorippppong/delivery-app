package com.sparta.foodorder.global.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * 한글 또는 영문 이름 검증기
 */
// ConstraintValidator = 검증하는 일을 하는 인터페이스
// <ValidKoreanOrEnglishName, String> = ValidKoreanOrEnglishName 어노테이션을 사용해서 String 타입을 검증한다는 뜻
public class KoreanOrEnglishNameValidator implements ConstraintValidator<ValidKoreanOrEnglishName, String> {
    
    // 정규식 = 글자 패턴을 찾는 공식 (마치 수학 공식 같음!)
    // 이 공식은 "한글 또는 영문 또는 공백만" 허용한다는 뜻
    // 가-힣 = 한글 완성형 글자들 (가, 나, 다, ... 힣)
    // a-zA-Z = 영문 대문자와 소문자
    // \\s = 공백 문자 (스페이스바)
    private static final Pattern NAME_PATTERN = Pattern.compile("^[가-힣a-zA-Z\\s]+$");
    
    // 실제로 이름을 검증하는 메서드
    // name = 검증할 이름
    // context = 검증할 때 필요한 추가 정보 (지금은 사용 안 함)
    @Override
    public boolean isValid(String name, ConstraintValidatorContext context) {
        // 이름이 아예 없거나 공백만 있으면 안 됨
        if (name == null || name.trim().isEmpty()) {
            return false;  // false = 검증 실패
        }
        
        // 정규식 패턴과 맞는지 확인
        // matches() = 패턴과 정확히 일치하는지 확인하는 메서드
        return NAME_PATTERN.matcher(name).matches();
    }
}

