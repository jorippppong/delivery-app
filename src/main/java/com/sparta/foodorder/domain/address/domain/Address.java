package com.sparta.foodorder.domain.address.domain;

import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 200)
    private String address;

    @Column(length = 100)
    private String detailAddress;

    @Column(length = 10)
    private String zipCode;

    @Column(length = 50)
    private String recipientName;

    @Column(length = 20)
    private String recipientPhone;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @Builder
    public Address(User user, String address, String detailAddress, String zipCode, 
                   String recipientName, String recipientPhone, Boolean isDefault) {
        this.user = user;
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    public void updateAddress(String address, String detailAddress, String zipCode) {
        this.address = address;
        this.detailAddress = detailAddress;
        this.zipCode = zipCode;
    }

    public void updateRecipient(String recipientName, String recipientPhone) {
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
    }

    public void setAsDefault() {
        this.isDefault = true;
    }

    public void setAsNotDefault() {
        this.isDefault = false;
    }
}
