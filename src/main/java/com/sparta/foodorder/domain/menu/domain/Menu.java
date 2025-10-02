package com.sparta.foodorder.domain.menu.domain;

import com.sparta.foodorder.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "p_menu")
public class Menu extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "menu_id", nullable = false, unique = true, updatable =false,  columnDefinition = "BINARY(16)")
    private UUID menuId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")//lob추가할것
    private String description;

    @Column(name = "price")
    private Integer price; //가격은 int로 ?

    //@ManyToOne(fetch = FetchType.LAZY)
    //@JoinColumn(name = "store_id")
    //@Column(name = "store_id")
    //private Store store;

    @Column(name = "is_hidden")
    private boolean isHidden;

    @Column(name = "is_active")
    private boolean isActive;

    @OneToMany(mappedBy = "menu", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    @Builder
    public Menu(String name, String description, Integer price, Long storeId,
                Boolean isHidden, Boolean isActive) {
        this.name = name;
        this.description = description;
        this.price = price;
        //this.storeId = storeId;
        this.isHidden = isHidden != null ? isHidden : false;
        this.isActive = isActive != null ? isActive : true;

    }


}
