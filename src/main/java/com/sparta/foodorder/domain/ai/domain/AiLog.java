package com.sparta.foodorder.domain.ai.domain;

import com.sparta.foodorder.global.common.BaseCreateEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "p_ai_log")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiLog extends BaseCreateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "ai_model", length = 50, nullable = false)
    private String aiModel;

    @Column(name = "product_name", length = 100)
    private String productName;

    @Column(name = "request_content")
    private String requestContent;

    @Column(name = "request_type", length = 50, nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestType requestType;

    @Column(name = "response_content", nullable = false)
    private String responseContent;

    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status = Status.SUCCESS;

    public enum RequestType {
        MENU_DESCRIPTION,
        MENU_RECOMMENDATION
    }

    public enum Status {
        SUCCESS,
        FAILED,
        TIMEOUT
    }
}
