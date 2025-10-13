package com.sparta.foodorder.domain.menu.presentation.dto;

import com.sparta.foodorder.domain.menu.domain.Menu;
import lombok.Getter;

import java.util.UUID;

@Getter
public class MenuResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Integer price; //가격은 int로 ?
    private boolean hidden;
    private boolean active;
    private UUID storeId;

    private MenuResponseDto(UUID id, String name, String description, Integer price, boolean hidden, boolean active, UUID storeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.hidden = hidden;
        this.active = active;
        this.storeId = storeId;

    }

    public static MenuResponseDto from(Menu menu) {
        return new MenuResponseDto(
                menu.getId(),
                menu.getName(),
                menu.getDescription(),
                menu.getPrice(),
                menu.isHidden(),
                menu.isActive(),
                menu.getStore().getId()
        );
    }
}
