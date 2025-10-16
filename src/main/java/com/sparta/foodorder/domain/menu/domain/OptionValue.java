package com.sparta.foodorder.domain.menu.domain;

import com.sparta.foodorder.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_option_value")
public class OptionValue extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "description")
    private String description;

    @Column(name = "add_price")
    private Integer addPrice;


    private OptionValue(Option option, String value, String description,
                       Integer addPrice) {
        this.option = option;
        this.value = value;
        this.description = description;
        this.addPrice = addPrice != null ? addPrice : 0;

    }

    public static OptionValue create(Option option, String value, String description, Integer addPrice) {
        return new OptionValue(option, value, description, addPrice);
    }

    public void updateOptionValue(String value, String description, Integer addPrice) {
        if(value != null) this.value = value;
        if(description != null) this.description = description;
        if(addPrice != null) this.addPrice = addPrice;
    }

    public void delete(String deletedBy) {
        this.softDelete(deletedBy);
    }
}
