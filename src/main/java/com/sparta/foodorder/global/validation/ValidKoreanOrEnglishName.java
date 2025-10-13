package com.sparta.foodorder.global.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 한글 또는 영문 이름만 허용하는 검증 어노테이션
 */
// 이 어노테이션을 어디에 붙일 수 있는지 정하는 것
// FIELD = 클래스 안의 변수에 붙일 수 있음
// PARAMETER = 메서드의 입력값에 붙일 수 있음
@Target({ElementType.FIELD, ElementType.PARAMETER})

// 이 어노테이션 정보를 언제까지 기억할지 정하는 것
// RUNTIME = 프로그램이 실행되는 동안 계속 기억함 (검증할 때 필요!)
@Retention(RetentionPolicy.RUNTIME)

// 실제로 검증하는 일을 누가 할지 정하는 것
// KoreanOrEnglishNameValidator가 실제 검증 작업을 함
@Constraint(validatedBy = KoreanOrEnglishNameValidator.class)

// 이 어노테이션에 대한 설명을 문서에 포함시킬지 정하는 것
@Documented
public @interface ValidKoreanOrEnglishName {
    
    // 검증에 실패했을 때 보여줄 에러 메시지
    // default = 기본값 (아무것도 안 적으면 이 메시지가 나옴)
    String message() default "이름은 한글 또는 영문만 입력 가능합니다";
    
    // 검증 그룹 (지금은 사용 안 함)
    Class<?>[] groups() default {};
    
    // 추가 정보 (지금은 사용 안 함)
    Class<? extends Payload>[] payload() default {};
}

