package com.sparta.foodorder.domain.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    
    private final RedisTemplate<String, String> redisTemplate;
    
    private static final String ACCESS_TOKEN_BLACKLIST_PREFIX = "blacklist:access:";
    private static final String REFRESH_TOKEN_BLACKLIST_PREFIX = "blacklist:refresh:";
    
    public void addAccessTokenToBlacklist(String token, long expirationSeconds) {
        String key = ACCESS_TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationSeconds, TimeUnit.SECONDS);
        log.info("Access Token이 블랙리스트에 추가되었습니다. (만료: {}초)", expirationSeconds);
    }
    
    public void addRefreshTokenToBlacklist(String token, long expirationSeconds) {
        String key = REFRESH_TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.opsForValue().set(key, "blacklisted", expirationSeconds, TimeUnit.SECONDS);
        log.info("Refresh Token이 블랙리스트에 추가되었습니다. (만료: {}초)", expirationSeconds);
    }
    
    public boolean isAccessTokenBlacklisted(String token) {
        String key = ACCESS_TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public boolean isRefreshTokenBlacklisted(String token) {
        String key = REFRESH_TOKEN_BLACKLIST_PREFIX + token;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }
    
    public void removeAccessTokenFromBlacklist(String token) {
        String key = ACCESS_TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.delete(key);
        log.info("Access Token이 블랙리스트에서 제거되었습니다.");
    }
    
    public void removeRefreshTokenFromBlacklist(String token) {
        String key = REFRESH_TOKEN_BLACKLIST_PREFIX + token;
        redisTemplate.delete(key);
        log.info("Refresh Token이 블랙리스트에서 제거되었습니다.");
    }
}

