package com.example.ticketing.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {
    private String message;
    private int status;
    private String code;

    @Builder
    public ErrorResponse(ErrorCode errorCode) {
        this.message = errorCode.getMessage();
        this.status = errorCode.getStatus().value();
        this.code = errorCode.name();
    }
}
