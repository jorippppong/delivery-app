package com.sparta.foodorder.domain.auth.infrastructure;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import jakarta.servlet.http.Cookie;
import java.io.IOException;
import org.springframework.security.core.Authentication;




@Component
@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        log.info("[JwtAuthFilter] Received Request: Method={}, URI={}", request.getMethod(), requestURI);

        if (!requestURI.startsWith("/v1/") || 
            (requestURI.startsWith("/v1/auth/") && !requestURI.equals("/v1/auth/me")) ||  
            requestURI.startsWith("/v1/auth/login") ||  
            requestURI.startsWith("/v1/auth/logout") ||
            (requestURI.equals("/v1/users") && request.getMethod().equals("POST")) ||
            requestURI.startsWith("/v1/users/userreg") ||  
            requestURI.startsWith("/v1/users/check-") ||  
            requestURI.contains("/v1/signup") ||  
            requestURI.contains("/v1/login") ) {  
            log.info("[JwtAuthFilter] Skipping authentication for public endpoint: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String accessToken = getAccessToken(request);
        String refreshToken = getRefreshToken(request);

        if (accessToken == null && refreshToken == null) {
            log.info("[JwtAuthFilter] No tokens found, proceeding unauthenticated for: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        if (accessToken == null && refreshToken != null) {
            try {
                accessToken = jwtAuthenticationProvider.genNewAccessToken(refreshToken);

                Cookie newAccessTokenCookie = new Cookie("accessToken", accessToken);
                newAccessTokenCookie.setHttpOnly(true);
                newAccessTokenCookie.setPath("/");
                newAccessTokenCookie.setMaxAge(Math.toIntExact(JwtTokenizer.ACCESS_TOKEN_EXPIRE_COUNT / 1000));
                response.addCookie(newAccessTokenCookie);
                response.setHeader("X-Token-Refreshed", "true");
            } catch (Exception e) {
                log.warn("Failed to refresh access token with refresh token: {}", refreshToken, e);
            }
        }

        if (accessToken != null) {
            try {
                Authentication authentication = jwtAuthenticationProvider.getAuthentication(accessToken);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                log.warn("JWT Authentication failed for token: {}. Error: {}", accessToken, e.getMessage());
                SecurityContextHolder.clearContext();
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"error\": \"인증 실패: " + e.getMessage() + "\"}");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }



     private String getAccessToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getRefreshToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}