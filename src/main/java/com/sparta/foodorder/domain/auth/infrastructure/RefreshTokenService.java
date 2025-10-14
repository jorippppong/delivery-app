package com.sparta.foodorder.domain.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String REFRESH_TOKEN_PREFIX = "refresh_token:";
    
    
    public void saveRefreshToken(String userEmail, String token, long expirationSeconds) {
        String key = REFRESH_TOKEN_PREFIX + userEmail;
        redisTemplate.opsForValue().set(key, token, expirationSeconds, TimeUnit.SECONDS);
        log.info("Refresh Token 저장: {}", userEmail);
    }
    
    public String getRefreshToken(String userEmail) {
        String key = REFRESH_TOKEN_PREFIX + userEmail;
        return redisTemplate.opsForValue().get(key);
    }
    
    public void deleteRefreshToken(String userEmail) {
        String key = REFRESH_TOKEN_PREFIX + userEmail;
        redisTemplate.delete(key);
        log.info("Refresh Token 삭제: {}", userEmail);
    }
    
    public boolean validateRefreshToken(String userEmail, String requestToken) {
        String storedToken = getRefreshToken(userEmail);
        
        if (storedToken == null) {
            log.warn("저장된 Refresh Token이 없습니다: {}", userEmail);
            return false;
        }
        
        if (!storedToken.equals(requestToken)) {
            log.warn("Refresh Token 불일치 (재사용 의심): {}", userEmail);
            return false;
        }
        
        return true;
    }
    
    public boolean existsRefreshToken(String userEmail) {
        String key = REFRESH_TOKEN_PREFIX + userEmail;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
}

