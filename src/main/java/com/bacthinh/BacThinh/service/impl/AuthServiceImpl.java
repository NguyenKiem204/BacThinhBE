package com.bacthinh.BacThinh.service.impl;

import com.bacthinh.BacThinh.config.JwtProperties;
import com.bacthinh.BacThinh.dto.request.LoginRequest;
import com.bacthinh.BacThinh.dto.request.RegisterRequest;
import com.bacthinh.BacThinh.dto.response.AuthResponse;
import com.bacthinh.BacThinh.entity.RefreshToken;
import com.bacthinh.BacThinh.entity.User;
import com.bacthinh.BacThinh.entity.UserRole;
import com.bacthinh.BacThinh.entity.UserStatus;
import com.bacthinh.BacThinh.exception.PendingApprovalException;
import com.bacthinh.BacThinh.exception.TokenException;
import com.bacthinh.BacThinh.exception.UserAccountStatusException;
import com.bacthinh.BacThinh.exception.UserExistException;
import com.bacthinh.BacThinh.repository.RefreshTokenRepository;
import com.bacthinh.BacThinh.repository.UserRepository;
import com.bacthinh.BacThinh.service.AuthService;
import com.bacthinh.BacThinh.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = (User) authentication.getPrincipal();

        userRepository.updateLastLogin(user.getId(), LocalDateTime.now());

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        addRefreshTokenCookie(response, refreshToken);

        return buildAuthResponse(user, accessToken, null);
    }


    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserExistException("Email already exists");
        }

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .role(UserRole.GIAO_DAN)
                .build();

        user = userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        saveRefreshToken(user, refreshToken);

        addRefreshTokenCookie(response, refreshToken);

        return buildAuthResponse(user, accessToken, null);
    }

    @Transactional
    public AuthResponse refreshToken(String oldRefreshToken, HttpServletResponse response) {
        RefreshToken storedToken = refreshTokenRepository.findByToken(oldRefreshToken)
                .orElseThrow(() -> new TokenException("Invalid refresh token"));

//        HttpServletRequest currentRequest = getCurrentRequest();
//        String currentIpAddress = currentRequest != null ? getClientIpAddress(currentRequest) : "Unknown";
//
//        if (!storedToken.getIpAddress().equals(currentIpAddress)) {
//            log.warn("Refresh token used from different IP. Revoking token. Original IP: {}, Current IP: {}",
//                    storedToken.getIpAddress(), currentIpAddress);
//            refreshTokenRepository.revokeToken(oldRefreshToken);
//            throw new TokenException("Refresh token used from unauthorized location.");
//        }

        if (!storedToken.isValid()) {
            refreshTokenRepository.revokeToken(oldRefreshToken);
            throw new TokenException("Refresh token is expired or revoked");
        }

        User user = storedToken.getUser();

        if (user.getStatus() == UserStatus.PENDING) {
            refreshTokenRepository.revokeToken(oldRefreshToken);
            throw new PendingApprovalException("Account is pending approval. Please wait for admin approval.");
        } else if (user.getStatus() != UserStatus.ACTIVE) {
            refreshTokenRepository.revokeToken(oldRefreshToken);
            throw new UserAccountStatusException("Your account is " + user.getStatus().name().toLowerCase() + ". Please contact support.");
        }

        String newAccessToken = jwtService.generateAccessToken(user);
        String newRefreshToken = jwtService.generateRefreshToken(user);

        refreshTokenRepository.revokeToken(oldRefreshToken);
        saveRefreshToken(user, newRefreshToken);

        addRefreshTokenCookie(response, newRefreshToken);

        return buildAuthResponse(user, newAccessToken, null);
    }

    @Transactional
    public void logout(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken)
                .ifPresent(token -> refreshTokenRepository.revokeToken(refreshToken));
    }

    @Transactional
    public void logoutAll(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        refreshTokenRepository.revokeAllUserTokens(user);
    }

    private void saveRefreshToken(User user, String token) {
        HttpServletRequest request = getCurrentRequest();

        refreshTokenRepository.revokeAllUserTokens(user);

        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiresAt(LocalDateTime.now().plusSeconds(jwtProperties.getRefreshTokenExpiration() / 1000))
                .deviceInfo(request != null ? request.getHeader("User-Agent") : "Unknown")
                .ipAddress(request != null ? getClientIpAddress(request) : "Unknown")
                .build();

        refreshTokenRepository.save(refreshToken);
    }

    private void addRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        long maxAgeSeconds = jwtProperties.getRefreshTokenExpiration() / 1000;
        jakarta.servlet.http.Cookie cookie = new jakarta.servlet.http.Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge((int) maxAgeSeconds);
        cookie.setSecure(false);
        response.addCookie(cookie);
        String cookieValue = String.format("%s=%s; Path=/; HttpOnly; Max-Age=%d; SameSite=Lax",
                "refreshToken",
                refreshToken,
                maxAgeSeconds);
        response.addHeader("Set-Cookie", cookieValue);
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {


        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .lastLogin(user.getLastLogin())
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtProperties.getAccessTokenExpiration() / 1000)
                .user(userInfo)
                .build();
    }

    private HttpServletRequest getCurrentRequest() {
        try {
            return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}

