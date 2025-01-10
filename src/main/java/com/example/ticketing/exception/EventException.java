package com.example.ticketing.exception;

import lombok.Getter;

@Getter
public class EventException extends RuntimeException {
    private final ErrorCode errorCode;

    public EventException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
