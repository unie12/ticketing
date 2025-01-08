package com.example.ticketing.security;

import com.example.ticketing.exception.AuthException;
import com.example.ticketing.exception.ErrorCode;
import org.springframework.stereotype.Component;

@Component
public class PasswordValidator {
    public void validatePassword(String password) {
        if (password.length() < 8 || password.length() > 20) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (!password.matches(".*[A-Z].*")) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (!password.matches(".*[a-z].*")) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (!password.matches(".*[0-9].*")) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
        if (!password.matches(".*[!@#$%^&*()].*")) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD_FORMAT);
        }
    }
}
