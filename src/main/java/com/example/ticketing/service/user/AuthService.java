package com.example.ticketing.service.user;

import com.example.ticketing.exception.AuthException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.model.user.LoginRequest;
import com.example.ticketing.model.user.LoginResponse;
import com.example.ticketing.model.user.SignUpRequest;
import com.example.ticketing.model.user.User;
import com.example.ticketing.repository.user.UserRepository;
import com.example.ticketing.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;


    public void registerUser(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthException(ErrorCode.DUPLICATE_USERNAME);
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        user.generateVerificationToken();

        try {
            userRepository.save(user);
            emailService.sendVerificationEmail(user, user.getVerificationToken());
        } catch (Exception e) {
            throw new AuthException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    public void verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new AuthException(ErrorCode.INVALID_TOKEN));

        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new AuthException(ErrorCode.TOKEN_EXPIRED);
        }

        user.verifyEmail();
        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if (!user.isEmailVerified()) {
            throw new AuthException(ErrorCode.EMAIL_NOT_VERIFIED);
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AuthException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtTokenProvider.generateToken(user);

        return new LoginResponse(token);
    }
}
