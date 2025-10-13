package com.sparta.foodorder.domain.ai.application;

import org.springframework.stereotype.Component;

@Component
public class PromptBuilder {
    private static final String SYSTEM_INSTRUCTION = "당신은 배달 음식 플랫폼의 전문 마케팅 작가입니다. " + "고객의 식욕을 자극하고 주문을 유도하는 매력적인 상품 설명을 작성합니다.";

    public String buildProductGeneratePrompt(String productName, String content) {
        return String.format("""
                        %s
                        다음 음식 상품에 대한 매력적이고 식욕을 돋우는 설명을 30-50자 이내로 작성해주세요.
                        다른 주제는 절대 다루지 않습니다.
                        상품명: %s
                        상품설명: %s
                        작성 가이드:
                        - 고객의 오감을 자극하는 표현 사용
                        - 긍정적이고 활기찬 어조
                        - 과장되지 않은 현실적인 표현
                        - 이모지나 특수문자 사용 금지
                        - 비속어 금지
                        - 불법으로 포획, 유통될 가능성이 높은 메뉴(보신탕, 개구리 등) 금지
                        - 음료를 제외한 공산품(마스크) 등 금지
                        """,
                SYSTEM_INSTRUCTION, productName, content);
    }
}
