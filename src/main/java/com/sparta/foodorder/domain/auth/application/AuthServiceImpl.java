package com.sparta.foodorder.domain.auth.application;

import com.sparta.foodorder.domain.auth.application.dto.LoginRequestDto;
import com.sparta.foodorder.domain.auth.application.dto.LoginResponseDto;
import com.sparta.foodorder.domain.auth.domain.AuthException;
import com.sparta.foodorder.domain.auth.domain.AuthService;
import com.sparta.foodorder.domain.auth.infrastructure.JwtTokenizer;
import com.sparta.foodorder.domain.auth.infrastructure.TokenBlacklistService;
import com.sparta.foodorder.domain.auth.infrastructure.RefreshTokenService;
import com.sparta.foodorder.domain.user.domain.User;
import com.sparta.foodorder.domain.user.domain.UserStatus;
import com.sparta.foodorder.domain.user.infrastructure.PasswordEncoderImpl;
import com.sparta.foodorder.domain.user.infrastructure.UserRepository;
import com.sparta.foodorder.global.exception.ErrorCode;

import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthServiceImpl implements AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoderImpl passwordEncoder;
    private final JwtTokenizer jwtTokenizer;
    private final TokenBlacklistService tokenBlacklistService;
    private final RefreshTokenService refreshTokenService;
    
    @Override
    @Transactional
    public LoginResponseDto login(LoginRequestDto requestDto) {
        log.info("로그인 시도: {}", requestDto.getUserEmail());
        
        User user = userRepository.findByUserEmail(requestDto.getUserEmail())
                .orElseThrow(() -> {
                    log.warn("존재하지 않는 사용자: {}", requestDto.getUserEmail());
                    return new AuthException(ErrorCode.USER_NOT_FOUND);
                });
        
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            log.warn("비밀번호 불일치: {}", requestDto.getUserEmail());
            throw new AuthException(ErrorCode.INVALID_PASSWORD);
        }
        
        // 사용자 상태 검증 
        validateUserStatus(user);
        
        String accessToken = jwtTokenizer.createAccessToken(user.getId(), user.getUserEmail(), user.getStatus(), user.getRoles());
        String refreshToken = jwtTokenizer.createRefreshToken(user.getId(), user.getUserEmail(), user.getStatus(), user.getRoles());
        
        // Refresh Token을 Redis에 저장 (Token Rotation 지원)
        long refreshTokenExpiration = JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000; // 밀리초 → 초
        refreshTokenService.saveRefreshToken(user.getUserEmail(), refreshToken, refreshTokenExpiration);
        
        log.info("로그인 성공: {}", requestDto.getUserEmail());
        return new LoginResponseDto(accessToken, refreshToken, user.getUserEmail(), user.getRole().name());
    }
    
    @Override
    public Optional<User> findByEmailWithRoles(String userEmail) {
        return userRepository.findByUserEmail(userEmail);
    }
    
    @Override
    @Transactional
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("로그아웃 요청 처리 시작");
        
        String accessToken = getCookieValue(request, "accessToken");
        String refreshToken = getCookieValue(request, "refreshToken");
        
        if (accessToken != null && !accessToken.isEmpty()) {
            try {
                Claims claims = jwtTokenizer.parseAccessToken(accessToken);
                String userEmail = claims.getSubject();
                
                User user = userRepository.findByUserEmail(userEmail)
                        .orElse(null);
                
                if (user != null) {
                    // Access Token을 블랙리스트에 추가
                    long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
                    long expirationSeconds = Math.max(expirationTime / 1000, 0);
                    tokenBlacklistService.addAccessTokenToBlacklist(accessToken, expirationSeconds);
                    
                    // Refresh Token을 Redis에서 삭제
                    refreshTokenService.deleteRefreshToken(userEmail);
                    
                    // Refresh Token도 블랙리스트에 추가
                    if (refreshToken != null && !refreshToken.isEmpty()) {
                        long refreshExpiration = JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000;
                        tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken, refreshExpiration);
                    }
                    
                    log.info("로그아웃 성공: {}", userEmail);
                } else {
                    log.warn("로그아웃 시도 - 존재하지 않는 사용자: {}", userEmail);
                }
            } catch (Exception e) {
                log.warn("로그아웃 처리 중 Access Token 파싱 실패: {}", e.getMessage());
            }
        } else {
            log.warn("로그아웃 시도 - AccessToken 쿠키를 찾을 수 없음");
        }
        
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
            log.info("HTTP 세션 무효화 완료");
        }
        
        clearCookies(response);
        
        log.info("로그아웃 프로세스 완료");
        return "로그아웃 완료";
    }
    

    @Override
    @Transactional
    public LoginResponseDto refresh(String token) {
        log.info("토큰 갱신 요청");
        String refreshToken = token.replace("Bearer ", "");
        
        try {
            // Refresh Token 블랙리스트 확인
            if (tokenBlacklistService.isRefreshTokenBlacklisted(refreshToken)) {
                log.warn("블랙리스트에 등록된 Refresh Token 사용 시도");
                throw new AuthException(ErrorCode.INVALID_TOKEN);
            }
            
            // Refresh Token 파싱 및 검증
            Claims claims = jwtTokenizer.parseRefreshToken(refreshToken);
            String userEmail = claims.getSubject();
            
            User user = userRepository.findByUserEmail(userEmail)
                    .orElseThrow(() -> {
                        log.warn("토큰 갱신 실패 - 존재하지 않는 사용자: {}", userEmail);
                        return new AuthException(ErrorCode.USER_NOT_FOUND);
                    });
            
            // Refresh Token 재사용 감지 (Token Rotation)
            if (!refreshTokenService.validateRefreshToken(userEmail, refreshToken)) {
                log.error("Refresh Token 재사용 감지! 보안 위험 - 모든 토큰 무효화: {}", userEmail);
                
                // 기존 Refresh Token 삭제
                refreshTokenService.deleteRefreshToken(userEmail);
                
                // 재사용된 토큰을 블랙리스트에 추가
                long refreshExpiration = JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000;
                tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken, refreshExpiration);
                
                throw new AuthException(ErrorCode.REFRESH_TOKEN_REUSED);
            }
            
            // 사용자 상태 검증
            validateUserStatus(user);
            
            // 새로운 Access Token 생성
            String newAccessToken = jwtTokenizer.createAccessToken(user.getId(), user.getUserEmail(), user.getStatus(), user.getRoles());
            
            // 새로운 Refresh Token 생성 
            String newRefreshToken = jwtTokenizer.createRefreshToken(user.getId(), user.getUserEmail(), user.getStatus(), user.getRoles());
            
            // 기존 Refresh Token을 블랙리스트에 추가 
            long refreshExpiration = JwtTokenizer.REFRESH_TOKEN_EXPIRE_COUNT / 1000;
            tokenBlacklistService.addRefreshTokenToBlacklist(refreshToken, refreshExpiration);
            
            // 새로운 Refresh Token을 Redis에 저장
            refreshTokenService.saveRefreshToken(userEmail, newRefreshToken, refreshExpiration);
            
            log.info("토큰 갱신 성공 (Token Rotation 적용): {}", userEmail);
            return new LoginResponseDto(newAccessToken, newRefreshToken, user.getUserEmail(), user.getRole().name());
            
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.error("토큰 만료: {}", e.getMessage());
            throw new AuthException(ErrorCode.TOKEN_EXPIRED);
        } catch (io.jsonwebtoken.security.SignatureException e) {
            log.error("토큰 서명 검증 실패 (변조 의심): {}", e.getMessage());
            throw new AuthException(ErrorCode.TOKEN_TAMPERED);
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            log.error("잘못된 형식의 토큰: {}", e.getMessage());
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        } catch (AuthException e) {
            throw e;
        } catch (Exception e) {
            log.error("토큰 갱신 실패: {}", e.getMessage());
            throw new AuthException(ErrorCode.INVALID_TOKEN);
        }
    }
    
    private String getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookieName.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    

    private void clearCookies(HttpServletResponse response) {

        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .path("/")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .sameSite("Strict")
                .secure(true)
                .httpOnly(true)
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        
        log.info("쿠키 삭제 완료");
    }
    

    private void validateUserStatus(User user) {
        if (user.getStatus() == UserStatus.WITHDRAWN) {
            log.warn("탈퇴한 사용자 접근 시도: {}", user.getUserEmail());
            throw new AuthException(ErrorCode.USER_WITHDRAWN);
        }
        
        if (user.getStatus() == UserStatus.BANNED) {
            log.warn("제재된 사용자 접근 시도: {}", user.getUserEmail());
            throw new AuthException(ErrorCode.USER_BANNED);
        }
        
        if (user.getStatus() != UserStatus.ACTIVE) {
            log.warn("비활성 사용자 접근 시도: {}", user.getUserEmail());
            throw new AuthException(ErrorCode.UNAUTHORIZED_USER);
        }
        
        if (Boolean.TRUE.equals(user.getIsDeleted())) {
            log.warn("삭제된 사용자 접근 시도: {}", user.getUserEmail());
            throw new AuthException(ErrorCode.USER_DEACTIVATED);
        }
    }
}
