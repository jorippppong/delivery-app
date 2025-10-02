package com.sparta.foodorder.domain.menu.domain;

import com.sparta.foodorder.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_option_value")
public class OptionValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_value_id", unique = true, nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID optionValueId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "description")
    private String description;

    @Column(name = "add_price")
    private Integer addPrice;

    @Builder
    public OptionValue(Option option, String value, String description,
                       Integer addPrice, String createdBy) {
        this.option = option;
        this.value = value;
        this.description = description;
        this.addPrice = addPrice != null ? addPrice : 0;

    }

}
