package com.sparta.foodorder.domain.store.domain;

import com.sparta.foodorder.global.common.BaseEntity;
import jakarta.persistence.*;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.Point;

@Entity
@Table(name = "p_store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "store_public_id", nullable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID storePublicId;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreCategory> storeCategories = new ArrayList<>();

//    menu merge 전이라 임시 주석처리
//    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Menu> menus = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private Point location;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "rating_count")
    private Long ratingCount;

    @Column(name = "rating_avg")
    private Float ratingAvg;

    @Column(name = "min_order_amount", nullable = false)
    private Long minOrderAmount;

    @Column(name = "delivery_fee", nullable = false)
    private Long deliveryFee;

    @Column(name = "opens_at", nullable = false)
    private LocalTime opensAt;

    @Column(name = "closes_at", nullable = false)
    private LocalTime closesAt;

    private Store(Long ownerId, String name, String address, Point location,
        String phoneNumber, Long minOrderAmount, Long deliveryFee,
        LocalTime opensAt, LocalTime closesAt) {
        this.ownerId = ownerId;
        this.name = name;
        this.address = address;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.minOrderAmount = minOrderAmount;
        this.deliveryFee = deliveryFee;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    @PrePersist
    public void generateStorePublicId() {
        if (this.storePublicId == null) {
            this.storePublicId = UUID.randomUUID();
        }
    }
}
