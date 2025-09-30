package com.sparta.foodorder.domain.address.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.sparta.foodorder.domain.user.domain.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "p_address")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Address {

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

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @Builder
    public Address(User user, String addressName, String addressLine, 
                   String detailAddress, String postalCode, Boolean isDefault, Long createdBy) {
        this.user = user;
        this.addressName = addressName;
        this.addressLine = addressLine;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
        this.isDefault = isDefault;
        this.createdBy = createdBy;
    }

    public void updateAddress(String addressName, String addressLine, 
                             String detailAddress, String postalCode, Long updatedBy) {
        this.addressName = addressName;
        this.addressLine = addressLine;
        this.detailAddress = detailAddress;
        this.postalCode = postalCode;
        this.updatedBy = updatedBy;
    }

    public void setAsDefault() {
        this.isDefault = true;
    }

    public void setAsNotDefault() {
        this.isDefault = false;
    }

    public void softDelete(Long deletedBy) {
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }
}
