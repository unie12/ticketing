package com.example.ticketing.exception;

import lombok.Getter;

@Getter
public class RecruitmentException extends RuntimeException {
    private final ErrorCode errorCode;

    public RecruitmentException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
