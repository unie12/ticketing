package com.example.ticketing.controller.auth;

import com.example.ticketing.exception.AuthException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.model.auth.LoginRequest;
import com.example.ticketing.model.auth.RefreshTokenRequest;
import com.example.ticketing.model.auth.SignUpRequest;
import com.example.ticketing.model.auth.TokenResponse;
import com.example.ticketing.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<String> registerUser(@Valid @RequestBody SignUpRequest request) {
        authService.registerUser(request);
        return ResponseEntity.ok("회원가입이 완료되었습니다. 이메일을 확인해주세요");
    }

    @GetMapping("/email-verification")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        try {
            authService.verifyEmail(token);
            return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
        } catch (AuthException e) {
            if (e.getErrorCode() == ErrorCode.TOKEN_EXPIRED) {
                return ResponseEntity.status(HttpStatus.GONE)
                        .body("인증 링크가 만료되었습니다. 새로운 인증 메일을 요청해주세요.");
            }
            throw e;
        }
    }

    @PostMapping("/email-verification/resend")
    public ResponseEntity<String> resendVerificationEmail(@RequestBody String request) {
        authService.resendVerificationEmail(request);
        return ResponseEntity.ok("새로운 인증 이메일이 발송되었습니다.");

    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        TokenResponse login = authService.login(request);
        return ResponseEntity.ok(login);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token.substring(7));
        return ResponseEntity.ok("로그아웃 되었습니다");
    }

    @PostMapping("/tokens/refresh")
    public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

}
