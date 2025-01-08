package com.example.ticketing.controller.auth;

import com.example.ticketing.model.user.LoginRequest;
import com.example.ticketing.model.user.LoginResponse;
import com.example.ticketing.model.user.SignUpRequest;
import com.example.ticketing.service.user.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        authService.verifyEmail(token);
        return ResponseEntity.ok("이메일 인증이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse login = authService.login(request);
        return ResponseEntity.ok(login);
    }
}
