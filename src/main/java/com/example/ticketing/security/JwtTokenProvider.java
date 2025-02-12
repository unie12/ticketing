package com.example.ticketing.security;

import com.example.ticketing.model.user.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
//    private final TokenService tokenService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostConstruct
    protected void init() {
        // 안전한 키 생성
        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        this.jwtSecret = Base64.getEncoder().encodeToString(key.getEncoded());
    }

    @Value("${jwt.expiration}")
    private Long jwtExpiration;

    private static final long REFRESH_TOKEN_EXPIRATION = 7 * 24 * 60 * 60 * 1000; // 7일

    public String generateRefreshToken(User user, String tokenFamily) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + REFRESH_TOKEN_EXPIRATION);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("tokenFamily", tokenFamily)
                .claim("tokenType", "REFRESH")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("roles", user.getRole().name())
                .claim("username", user.getUsername())
                .claim("tokenType", "ACCESS")
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return Long.parseLong(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
//            if (tokenService.isTokenBlacklisted(token)) {
//                return false;
//            }
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String tokenType = claims.get("tokenType", String.class);

            if (tokenType == null) {
                return false;
            }

            return true;
        } catch (SecurityException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public long getExpirationFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            Date expiration = claims.getExpiration();
            Date now = new Date();
            return expiration.getTime() - now.getTime();
        } catch (Exception e) {
            return 0;
        }
    }

    public String getTokenFamilyFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("tokenFamily", String.class);
    }

    public String getTokenTypeFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("tokenType", String.class);
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.get("username", String.class);
    }
}
