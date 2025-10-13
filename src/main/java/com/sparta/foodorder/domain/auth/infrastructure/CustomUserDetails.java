package com.sparta.foodorder.domain.auth.infrastructure;

import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.domain.user.domain.UserStatus;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {
    private final Long userId;
    private final String userEmail;
    private final String password;
    private final Set<UserRole> roles;
    private final UserStatus status;
    private final String name;
    private final String nickName;
    private final String userPhone;

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.userEmail = user.getUserEmail();
        this.password = user.getPassword();
        this.roles = user.getRoles();
        this.status = user.getStatus();
        this.name = user.getName();
        this.nickName = user.getNickName();
        this.userPhone = user.getUserPhone();
        System.out.println("💡 [DEBUG] 유저 생성됨: roles = " + this.roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return Collections.emptyList();
        }
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userEmail;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getName() {
        return name;
    }

    public String getNickName() {
        return nickName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public Set<UserRole> getRoles() {
        return roles;
    }

    public UserStatus getStatus() {
        return status;
    }

    @Override
    public boolean isAccountNonExpired() { 
        return status != UserStatus.WITHDRAWN; 
    }

    @Override
    public boolean isAccountNonLocked() { 
        return status != UserStatus.BANNED; 
    }

    @Override
    public boolean isCredentialsNonExpired() { 
        return true; 
    }

    @Override
    public boolean isEnabled() { 
        return status == UserStatus.ACTIVE; 
    }
}
