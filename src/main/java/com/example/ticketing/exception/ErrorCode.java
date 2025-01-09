package com.example.ticketing.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 기존 코드
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 사용자명입니다."),
    INVALID_TOKEN(HttpStatus.BAD_REQUEST, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.BAD_REQUEST, "만료된 토큰입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.BAD_GATEWAY, "서버 에러가 발생했습니다"),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "계정이 잠겼습니다. 30분 후에 다시 시도해주세요."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 8-20자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다."),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),

    // 인증/인가 관련 추가 에러
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),

    // 보안 관련 추가 에러
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),
    SUSPICIOUS_ACTIVITY(HttpStatus.FORBIDDEN, "의심스러운 활동이 감지되었습니다."),

    // 이메일 관련 추가 에러
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),
    VERIFICATION_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었습니다. 다시 시도해주세요."),

    // 세션 관련 추가 에러
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다. 다시 로그인해주세요."),
    CONCURRENT_SESSION(HttpStatus.UNAUTHORIZED, "다른 기기에서 로그인되었습니다."),

    // REDIS
    REMAINING_ATTEMPTS(HttpStatus.BAD_REQUEST, "로그인 실패. 남은 시도 횟수: %d회"),
    ACCOUNT_LOCKED_WITH_TIME(HttpStatus.UNAUTHORIZED, "계정이 잠겼습니다. %d분 후에 다시 시도해주세요."),

    TOKEN_REUSE_DETECTED(HttpStatus.UNAUTHORIZED, "토큰이 재사용되었습니다." ),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "잘못된 토큰 타입입니다.");


    private final HttpStatus status;
    private final String message;
}
