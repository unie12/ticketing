package com.example.ticketing.exception;

public class AuthException extends CustomException {
    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }
}
