package com.sparta.foodorder.domain.user.domain;

import com.sparta.foodorder.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Set;

@Entity
@Table(name = "p_user")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, length = 50)
    private String nickName;

    @Column(name = "user_email", nullable = false, unique = true, length = 100)
    private String userEmail;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "user_phone", nullable = false, length = 20)
    private String userPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role = UserRole.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Builder
    public User(String name, String nickName, String userEmail, String password, String userPhone, UserRole role, UserStatus status, Boolean isDeleted) {
        this.name = name;
        this.nickName = nickName;
        this.userEmail = userEmail;
        this.password = password;
        this.userPhone = userPhone;
        this.role = role != null ? role : UserRole.USER;
        this.status = status != null ? status : UserStatus.ACTIVE;
        this.isDeleted = isDeleted != null ? isDeleted : false;
    }

    public void updateProfile(String userEmail, String userPhone) {
        this.userEmail = userEmail;
        this.userPhone = userPhone;
    }

    public void changePassword(String newPassword) {
        this.password = newPassword;
    }

    public void deactivate() {
        this.status = UserStatus.INACTIVE;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
    }

    public void ban() {
        this.status = UserStatus.BANNED;
    }
    
    public Set<UserRole> getRoles() {
        return Set.of(this.role);
    }
}
