package com.example.ticketing.service.auth;

import com.example.ticketing.model.auth.TokenData;
import com.example.ticketing.security.JwtTokenProvider;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final String BLACKLIST_PREFIX = "BL:";
    private static final String TOKEN_FAMILY_PREFIX = "TF:";

    public void saveRefreshToken(Long userId, String refreshToken, String tokenFamily) {
        try {
            TokenData tokenData = new TokenData(refreshToken, tokenFamily);
            String tokenDataJson = objectMapper.writeValueAsString(tokenData);
            redisTemplate.opsForValue()
                    .set(REFRESH_TOKEN_PREFIX + userId, tokenDataJson, 7, TimeUnit.DAYS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("토큰 저장 실패", e);
        }
    }

    public TokenData getRefreshToken(Long userId) {
        try {
            String tokenDataJson = redisTemplate.opsForValue()
                    .get(REFRESH_TOKEN_PREFIX + userId);
            if (tokenDataJson == null) {
                return null;
            }
            return objectMapper.readValue(tokenDataJson, TokenData.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("토큰 조회 실패", e);
        }
    }

    public void invalidateTokenFamily(String tokenFamily) {
        redisTemplate.delete(TOKEN_FAMILY_PREFIX + tokenFamily);
    }

    public void invalidateRefreshToken(Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }


    public void addToBlacklist(String token) {
        log.info("Adding token to blacklist: {}", maskToken(token));

        redisTemplate.opsForValue()
                .set(BLACKLIST_PREFIX + token, "blacklisted",
                        jwtTokenProvider.getExpirationFromToken(token), TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + token));
    }

    private String maskToken(String token) {
        if (token.length() <= 10) return "*".repeat(token.length());

        return token.substring(0, 5)
                + "*".repeat(token.length() - 10)
                + token.substring(token.length() - 5);
    }
}
