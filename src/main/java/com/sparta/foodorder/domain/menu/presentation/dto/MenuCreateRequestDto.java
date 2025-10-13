package com.sparta.foodorder.domain.menu.presentation.dto;

import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.domain.store.domain.Store;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
public class MenuCreateRequestDto {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    @PositiveOrZero
    private Integer price;

    private boolean hidden = false;

    private boolean active = true;


    public Menu toEntity(Store store) {

        return Menu.create(
                this.name,
                this.description,
                this.price,
                store,
                this.hidden,
                this.active,
                null
        );


    }
}
