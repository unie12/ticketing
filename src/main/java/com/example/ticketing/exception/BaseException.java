package com.example.ticketing.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final Object[] args;

    protected BaseException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
        this.args = args;
    }
}
