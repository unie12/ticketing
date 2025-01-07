package com.example.ticketing.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // Auth 관련 에러
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 사용자명입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "서버 에러가 발생했습니다" ),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다.");

    private final HttpStatus status;
    private final String message;
}