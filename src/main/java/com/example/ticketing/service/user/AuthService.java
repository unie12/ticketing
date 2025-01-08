package com.example.ticketing.service.user;

import com.example.ticketing.exception.AuthException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.model.user.*;
import com.example.ticketing.repository.user.UserRepository;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.security.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.example.ticketing.service.user.LoginAttemptService.MAX_ATTEMPTS;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final LoginAttemptService loginAttemptService;
    private final PasswordValidator passwordValidator;
    private final TokenService tokenService;


    public void registerUser(SignUpRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AuthException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AuthException(ErrorCode.DUPLICATE_USERNAME);
        }

        passwordValidator.validatePassword(request.getPassword());

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

    public TokenResponse login(LoginRequest request) {
        String email = request.getEmail();

        if (loginAttemptService.isBlocked(email)) {
            Long remainingMinutes = loginAttemptService.getRemainingLockTime(email);
            throw new AuthException(ErrorCode.ACCOUNT_LOCKED_WITH_TIME, remainingMinutes);
        }

        try {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

            if (!user.isEmailVerified()) {
                throw new AuthException(ErrorCode.EMAIL_NOT_VERIFIED);
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                loginAttemptService.loginFailed(email);
                int attempts = loginAttemptService.getCurrentAttempts(email);
                int remainingAttempts = MAX_ATTEMPTS - attempts;
                if (remainingAttempts > 0) {
                    throw new AuthException(ErrorCode.REMAINING_ATTEMPTS, remainingAttempts);
                }
                throw new AuthException(ErrorCode.INVALID_PASSWORD);
            }

            loginAttemptService.loginSucceeded(email);
            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user);
            tokenService.saveRefreshToken(user.getId(), refreshToken);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (AuthException e) {
            throw e;
        }
    }


    public void logout(String token) {
        tokenService.addToBlacklist(token);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        Long userID = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        String newAccessToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user);

        tokenService.saveRefreshToken(userID, newRefreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
