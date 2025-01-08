package com.example.ticketing.service.user;

import com.example.ticketing.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;

    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue()
                .set("RT:" + userId, refreshToken, 7, TimeUnit.DAYS);
    }

    public void addToBlacklist(String token) {
        redisTemplate.opsForValue()
                .set("BL:" + token, "blacklisted",
                        jwtTokenProvider.getExpirationFromToken(token), TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey("BL:" + token));
    }
}
