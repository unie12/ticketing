package com.example.ticketing.model.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthProvider {
    LOCAL,
    GOOGLE
}