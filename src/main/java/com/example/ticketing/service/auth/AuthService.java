package com.example.ticketing.service.auth;

import com.example.ticketing.exception.AuthException;
import com.example.ticketing.exception.ErrorCode;
import com.example.ticketing.model.auth.LoginRequest;
import com.example.ticketing.model.auth.SignUpRequest;
import com.example.ticketing.model.auth.TokenData;
import com.example.ticketing.model.auth.TokenResponse;
import com.example.ticketing.model.user.*;
import com.example.ticketing.repository.user.UserRepository;
import com.example.ticketing.security.JwtTokenProvider;
import com.example.ticketing.security.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.example.ticketing.service.auth.LoginAttemptService.MAX_ATTEMPTS;

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
            throw new AuthException(ErrorCode.TOKEN_EXPIRED, "인증 링크가 만료되었습니다. 새로운 인증 이메일을 요청해주세요.");
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

            String tokenFamily = UUID.randomUUID().toString();
            String accessToken = jwtTokenProvider.generateToken(user);
            String refreshToken = jwtTokenProvider.generateRefreshToken(user, tokenFamily);

//            TokenData tokenData = new TokenData(refreshToken, tokenFamily);
            tokenService.saveRefreshToken(user.getId(), refreshToken, tokenFamily);

            return TokenResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .build();
        } catch (AuthException e) {
            throw e;
        }
    }


    public void logout(String token) {
        // 액세스 토큰 블랙리스트 추가
        tokenService.addToBlacklist(token);

        // 리프레시 토큰 무효화
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        tokenService.invalidateRefreshToken(userId);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new AuthException(ErrorCode.INVALID_REFRESH_TOKEN);
        }

        String tokenType = jwtTokenProvider.getTokenTypeFromToken(refreshToken);
        if (!"REFRESH".equals(tokenType)) {
            throw new AuthException(ErrorCode.INVALID_TOKEN_TYPE);
        }

        Long userID = jwtTokenProvider.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userID)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        String tokenFamily = jwtTokenProvider.getTokenFamilyFromToken(refreshToken);
        TokenData storedTokenData = tokenService.getRefreshToken(userID);

        if (storedTokenData == null || !storedTokenData.getTokenFamily().equals(tokenFamily)) {
            tokenService.invalidateTokenFamily(tokenFamily);
            throw new AuthException(ErrorCode.TOKEN_REUSE_DETECTED);
        }

        String newTokenFamily = UUID.randomUUID().toString();
        String newAccessToken = jwtTokenProvider.generateToken(user);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user, newTokenFamily);

//        TokenData newTokenData = new TokenData(newRefreshToken, newTokenFamily);
        tokenService.saveRefreshToken(userID, newRefreshToken, newTokenFamily);
        tokenService.addToBlacklist(refreshToken);

        return TokenResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void resendVerificationEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthException(ErrorCode.USER_NOT_FOUND));

        if (user.isEmailVerified()) {
            throw new AuthException(ErrorCode.EMAIL_ALREADY_VERIFIED);
        }

        user.generateVerificationToken();
        userRepository.save(user);
        emailService.sendVerificationEmail(user, user.getVerificationToken());
    }
}
