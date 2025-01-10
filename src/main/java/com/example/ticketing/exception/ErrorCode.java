package com.example.ticketing.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // 인증 관련 에러 (AUTH_XXX)
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증이 필요합니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
    TOKEN_BLACKLISTED(HttpStatus.UNAUTHORIZED, "사용할 수 없는 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 리프레시 토큰입니다."),
    INVALID_TOKEN_TYPE(HttpStatus.UNAUTHORIZED, "잘못된 토큰 타입입니다."),
    TOKEN_REUSE_DETECTED(HttpStatus.UNAUTHORIZED, "토큰이 재사용되었습니다."),

    // 계정 관련 에러 (ACCOUNT_XXX)
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    DUPLICATE_USERNAME(HttpStatus.BAD_REQUEST, "이미 사용 중인 사용자명입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    INVALID_PASSWORD_FORMAT(HttpStatus.BAD_REQUEST, "비밀번호는 8-20자의 영문 대/소문자, 숫자, 특수문자를 포함해야 합니다."),

    // 계정 잠금 관련 에러 (LOCK_XXX)
    ACCOUNT_LOCKED(HttpStatus.UNAUTHORIZED, "계정이 잠겼습니다. 30분 후에 다시 시도해주세요."),
    ACCOUNT_LOCKED_WITH_TIME(HttpStatus.UNAUTHORIZED, "계정이 잠겼습니다. %d분 후에 다시 시도해주세요."),
    REMAINING_ATTEMPTS(HttpStatus.BAD_REQUEST, "로그인 실패. 남은 시도 횟수: %d회"),

    // 이메일 인증 관련 에러 (EMAIL_XXX)
    EMAIL_NOT_VERIFIED(HttpStatus.FORBIDDEN, "이메일 인증이 필요합니다."),
    EMAIL_ALREADY_VERIFIED(HttpStatus.BAD_REQUEST, "이미 인증된 이메일입니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "이메일 발송에 실패했습니다."),
    VERIFICATION_TIME_EXPIRED(HttpStatus.BAD_REQUEST, "인증 시간이 만료되었습니다. 다시 시도해주세요."),

    // 접근 제어 관련 에러 (ACCESS_XXX)
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    TOO_MANY_REQUESTS(HttpStatus.TOO_MANY_REQUESTS, "요청이 너무 많습니다. 잠시 후 다시 시도해주세요."),
    SUSPICIOUS_ACTIVITY(HttpStatus.FORBIDDEN, "의심스러운 활동이 감지되었습니다."),

    // 세션 관련 에러 (SESSION_XXX)
    SESSION_EXPIRED(HttpStatus.UNAUTHORIZED, "세션이 만료되었습니다. 다시 로그인해주세요."),
    CONCURRENT_SESSION(HttpStatus.UNAUTHORIZED, "다른 기기에서 로그인되었습니다."),

    // 시스템 에러 (SYSTEM_XXX)
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 에러가 발생했습니다."),
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    // 쿠폰 관련 에러 (COUPON_XXX)
    COUPON_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    COUPON_ALREADY_OWNED(HttpStatus.BAD_REQUEST, "이미 발급받은 쿠폰입니다."),
    COUPON_OUT_OF_STOCK(HttpStatus.BAD_REQUEST, "쿠폰이 모두 소진되었습니다."),
    COUPON_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "사용할 수 없는 쿠폰입니다."),
    COUPON_UPDATE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "쿠폰 수량 업데이트에 실패했습니다."),

    // 쿠폰 템플릿 관련 에러 (COUPON_TEMPLATE_XXX)
    COUPON_TEMPLATE_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰 템플릿을 찾을 수 없습니다."),
    COUPON_TEMPLATE_NAME_EMPTY(HttpStatus.BAD_REQUEST, "쿠폰 템플릿 이름은 필수입니다."),
    INVALID_COUPON_WEIGHT(HttpStatus.BAD_REQUEST, "쿠폰 가중치는 0보다 커야 합니다."),
    INVALID_COUPON_QUANTITY(HttpStatus.BAD_REQUEST, "쿠폰 수량은 0보다 커야 합니다."),
    INVALID_DISCOUNT_AMOUNT(HttpStatus.BAD_REQUEST, "할인 금액은 0보다 커야 합니다."),

    // 이벤트 관련 에러 (EVENT_XXX)
    EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "이벤트를 찾을 수 없습니다."),
    EVENT_NAME_EMPTY(HttpStatus.BAD_REQUEST, "이벤트 이름은 필수입니다."),
    EVENT_SEAT_AMOUNT(HttpStatus.BAD_REQUEST, "이벤트 좌석은 0보다 커야 합니다."),


    // 쿠폰 이벤트 관련 에러 (COUPON_EVENT_XXX)
    COUPON_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "쿠폰 이벤트를 찾을 수 없습니다."),
    INVALID_EVENT_TIME_RANGE(HttpStatus.BAD_REQUEST, "이벤트 시작 시간은 종료 시간보다 이전이어야 합니다."),
    INVALID_VALIDITY_TIME_RANGE(HttpStatus.BAD_REQUEST, "이벤트 종료 시간은 유효 기간 종료 시간보다 이전이어야 합니다.");

    private final HttpStatus status;
    private final String message;
}
