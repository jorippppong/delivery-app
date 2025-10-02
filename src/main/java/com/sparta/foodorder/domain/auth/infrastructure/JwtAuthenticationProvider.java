package com.sparta.foodorder.domain.auth.infrastructure;

import com.sparta.foodorder.domain.auth.domain.AuthService;
import com.sparta.foodorder.domain.user.domain.User;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;

import java.util.Collection;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private final JwtTokenizer jwtTokenizer;
    private final AuthService authService;

    public User getUserFromClaims(Claims claims) {
        String userEmail = extractUserEmail(claims);
        User user = authService.findByEmailWithRoles(userEmail)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        switch (user.getStatus()) {
            case ACTIVE:
                break;
            case INACTIVE:
                log.warn("INACTIVE 계정으로 인증 시도: userEmail={}, status={}", userEmail, user.getStatus());
                throw new RuntimeException("비활성화된 계정입니다. 관리자에게 문의하세요.");
            case PENDING:
                log.warn("PENDING 계정으로 인증 시도: userEmail={}, status={}", userEmail, user.getStatus());
                throw new RuntimeException("계정 승인 대기 중입니다. 관리자에게 문의하세요.");
            case BANNED:
                log.warn("BANNED 계정으로 인증 시도: userEmail={}, status={}", userEmail, user.getStatus());
                throw new RuntimeException("제재된 계정입니다. 관리자에게 문의하세요.");
            case WITHDRAWN:
                log.warn("WITHDRAWN 계정으로 인증 시도: userEmail={}, status={}", userEmail, user.getStatus());
                throw new RuntimeException("이미 탈퇴한 계정입니다.");
            default:
                log.warn("알 수 없는 계정 상태로 인증 시도: userEmail={}, status={}", userEmail, user.getStatus());
                throw new RuntimeException("계정 상태를 확인할 수 없습니다. 관리자에게 문의하세요.");
        }

        return user;
    }

    public String genNewAccessToken(String refreshToken) {
        Claims claims;
        try {
            claims = jwtTokenizer.parseRefreshToken(refreshToken);
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다", e);
        }
        User user = getUserFromClaims(claims);
        return jwtTokenizer.createAccessToken(user.getId(), user.getUserEmail(), user.getStatus(), user.getRoles()); // 새 AccessToken 생성
    }

    public Authentication getAuthentication(String token) {
        Claims claims;
        try {
            claims = jwtTokenizer.parseAccessToken(token);
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("토큰이 만료되었습니다", e);
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다", e);
        }


        User user = getUserFromClaims(claims);

        Collection<GrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());

        return new JwtAuthenticationToken(authorities, new CustomUserDetails(user), null);
    }

    private String extractUserEmail(Claims claims) {
        String userEmail = claims.get("userEmail", String.class);
        if (userEmail == null || userEmail.isEmpty()) {
            throw new IllegalStateException("JWT에 userEmail 없음");
        }
        return userEmail;
    }

}