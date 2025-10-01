package com.sparta.foodorder.domain.address.domain;

import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_addresses")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Address extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "address_name", length = 50)
    private String addressName;

    @Column(name = "address_line", nullable = false, length = 255)
    private String addressLine;

    @Column(name = "detail_address", length = 255)
    private String detailAddress;

    @Column(name = "postal_code", length = 20)
    private String postalCode;

    @Column(length = 50)
    private String recipientName;

    @Column(length = 20)
    private String recipientPhone;

    @Column(nullable = false)
    private Boolean isDefault = false;

    @Builder
    public Address(User user, String addressName, String addressLine, String detailAddress, String postalCode, 
                   String recipientName, String recipientPhone, Boolean isDefault) {
        this.user = user;
        this.addressName = addressName;
        this.addressLine = addressLine;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
        this.recipientName = recipientName;
        this.recipientPhone = recipientPhone;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    public void updateAddress(String addressLine, String detailAddress, String postalCode) {
        this.addressLine = addressLine;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
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
