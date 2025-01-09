package com.example.ticketing.model.user;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class TokenData {
    private String refreshToken;
    private String tokenFamily;
    private LocalDateTime issuedAt;

    public TokenData(String refreshToken, String tokenFamily) {
        this.refreshToken = refreshToken;
        this.tokenFamily = tokenFamily;
        this.issuedAt = LocalDateTime.now();
    }
}