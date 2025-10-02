package com.sparta.foodorder.domain.auth.infrastructure.util;

import org.springframework.stereotype.Component;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.sparta.foodorder.domain.auth.infrastructure.CustomUserDetails;
import com.sparta.foodorder.domain.user.domain.User;

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
            return ((CustomUserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    /**
     * 현재 로그인한 User 엔티티 반환
     */
    public static User getCurrentUser() {
        if (!isLoggedIn()) return null;
        Object principal = getAuthentication().getPrincipal();
        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }
        return null;
    }
}
