package com.sparta.foodorder.domain.store.domain;

import com.sparta.foodorder.domain.menu.domain.Menu;
import com.sparta.foodorder.global.common.BaseEntity;
import com.sparta.foodorder.global.exception.BusinessException;
import com.sparta.foodorder.global.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.locationtech.jts.geom.*;
import org.locationtech.jts.geom.PrecisionModel;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "p_store")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "owner_id", nullable = false)
    private Long ownerId;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreCategory> storeCategories = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Menu> menus = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
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

    private Store(
        Long ownerId, String name, String description, String address, Point location,
        String phoneNumber, Long minOrderAmount, Long deliveryFee, LocalTime opensAt,
        LocalTime closesAt
    ) {
        this.ownerId = ownerId;
        this.name = name;
        this.description = description;
        this.address = address;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.minOrderAmount = minOrderAmount;
        this.deliveryFee = deliveryFee;
        this.opensAt = opensAt;
        this.closesAt = closesAt;
    }

    @PrePersist
    public void generateStoreId() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }

    public void validateOwner(Long userId) {
        if (!this.ownerId.equals(userId)) {
            throw new BusinessException(ErrorCode.ORDER_CANT_ACCESS);
        }
    }

    private static Point toPoint(double longitude, double latitude) {
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        return geometryFactory.createPoint(new Coordinate(longitude, latitude));
    }

    public static Store createStore(
        Long ownerId, String name, String description, String address,
        Double longitude, Double latitude, String phoneNumber, Long minOrderAmount,
        Long deliveryFee, LocalTime opensAt, LocalTime closesAt
    ) {
        Point location = toPoint(longitude, latitude);
        return new Store(ownerId, name, description, address, location, phoneNumber,
            minOrderAmount, deliveryFee, opensAt, closesAt);
    }

    public void addCategory(Category category) {
        storeCategories.add(StoreCategory.createStoreCategory(this, category));
    }

    public void updateStore(
        String name, String description, String address,
        Double longitude, Double latitude, String phoneNumber,
        Boolean isActive, Long minOrderAmount, Long deliveryFee,
        LocalTime opensAt, LocalTime closesAt
    ) {
        if(name != null) this.name = name;
        if(description != null) this.description = description;
        if(address != null) this.address = address;
        if(longitude != null && latitude != null) this.location = toPoint(longitude, latitude);
        if(phoneNumber != null) this.phoneNumber = phoneNumber;
        if(isActive != null) this.isActive = isActive;
        if(minOrderAmount != null) this.minOrderAmount = minOrderAmount;
        if(deliveryFee != null) this.deliveryFee = deliveryFee;
        if(opensAt != null) this.opensAt = opensAt;
        if(closesAt != null) this.closesAt = closesAt;
    }
}
