package com.sparta.foodorder.domain.auth.infrastructure.util;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.user.domain.UserRole;
import com.sparta.foodorder.domain.user.domain.UserStatus;

@Component
public class SecurityUtil {

    /**
     * 현재 로그인된 사용자의 Authentication 반환
     */
    private static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 현재 로그인 여부 확인
     */
    public static boolean isLoggedIn() {
        Authentication auth = getAuthentication();
        return auth != null && auth.isAuthenticated()
                && !(auth.getPrincipal() instanceof String && auth.getPrincipal().equals("anonymousUser"));
    }

    /**
     * 현재 로그인한 사용자의 email 반환
     */
    public static String getCurrentEmail() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserEmail();
        }
        return principal.toString();
    }

    /**
     * 현재 로그인한 사용자의 ID 반환
     */
    public static Long getCurrentUserId() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserId();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 이름 반환
     */
    public static String getCurrentUserName() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getName();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 닉네임 반환
     */
    public static String getCurrentUserNickName() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getNickName();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 전화번호 반환
     */
    public static String getCurrentUserPhone() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUserPhone();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 역할 반환
     */
    public static java.util.Set<UserRole> getCurrentUserRoles() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getRoles();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 상태 반환
     */
    public static UserStatus getCurrentUserStatus() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getStatus();
        }
        return null;
    }

    /**
     * 현재 로그인한 사용자의 CustomUserDetails 반환
     */
    public static CustomUserDetails getCurrentUserDetails() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return (CustomUserDetails) principal;
        }
        return null;
    }
}
