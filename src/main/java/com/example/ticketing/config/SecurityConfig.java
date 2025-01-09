package com.example.ticketing.config;

import com.example.ticketing.repository.user.UserRepository;
import com.example.ticketing.security.*;
import com.example.ticketing.service.user.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.lang.reflect.Array;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final TokenService tokenService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // JWT 기반 인증 사용 -> CSRF 보호 비활성화, stateless한 rest api에서 CSRF 공격 위험 낮음 
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // cors 설정 -> 허용된 출처에서만 접근
                .headers(headers -> headers // 보안 헤더 설정
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::deny) // 클릭재킹 방지
                        .xssProtection(Customizer.withDefaults()) // XSS 보호
                        .contentSecurityPolicy(csp -> // CSP 정ㅊㄱ
                                csp.policyDirectives("default-src 'self'; script-src 'self' 'unsafe-inline'; style-src 'self' 'unsafe-inline';"))
                        .httpStrictTransportSecurity(hsts -> // HTTPS 강제
                                hsts.includeSubDomains(true)
                                        .maxAgeInSeconds(31536000)))
                .sessionManagement(session -> // 세션 관리
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // JWT 사용을 위한 세션리스 설정
                .exceptionHandling(exception -> exception // 예외 처리
                        .authenticationEntryPoint(customAuthenticationEntryPoint()) // 인증 실패시 처리 (401)
                        .accessDeniedHandler(customAccessDeniedHandler())) // 권한 부족시 처리 (403)
                .authorizeHttpRequests(auth -> auth // url 접근 권한
                        .requestMatchers(
                                "/api/auth/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/error"
                        ).permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .anyRequest().authenticated())
                // 보안 필터 체인
                .addFilterBefore(new RequestLoggingFilter(), UsernamePasswordAuthenticationFilter.class) // 모든 요청 로깅
                .addFilterBefore(new RateLimitFilter(), UsernamePasswordAuthenticationFilter.class) // 초당 50개 요청 제한
                .addFilterBefore(new XssFilter(), UsernamePasswordAuthenticationFilter.class) // XSS 공격 방지
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 토큰 검증

        // H2 콘솔 사용을 위한 설정 (개발 환경only)
        http.headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
                "http://localhost:3000",  // React 기본 포트
                "http://localhost:8080"   // Spring Boot 기본 포트
        ));
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"
        ));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, userRepository, tokenService);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint customAuthenticationEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"인증 실패\", \"message\": \""
                    + authException.getMessage() + "\"}");
        };
    }

    @Bean
    public AccessDeniedHandler customAccessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"접근 거부\", \"message\": \""
                    + accessDeniedException.getMessage() + "\"}");
        };
    }

    // 정적 리소스 보안 설정
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/images/**",
                        "/js/**",
                        "/css/**",
                        "/webjars/**",
                        "/favicon.ico"
                );
    }
}