package com.bacthinh.BacThinh.controller;

import com.bacthinh.BacThinh.dto.request.LoginRequest;
import com.bacthinh.BacThinh.dto.request.RefreshTokenRequest;
import com.bacthinh.BacThinh.dto.request.RegisterRequest;
import com.bacthinh.BacThinh.dto.response.ApiResponse;
import com.bacthinh.BacThinh.dto.response.AuthResponse;
import com.bacthinh.BacThinh.entity.User;
import com.bacthinh.BacThinh.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Auth Controller", description = "Authentication and token management")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Login", description = "Login to the system, returns access token and refresh token.")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                           HttpServletResponse response) {
        log.info("Login request received for email: {}", request.getEmail());
        
        try {
            AuthResponse authResponse = authService.login(request, response);
            
            // Log response headers
            log.info("Login successful, response headers:");
            for (String headerName : response.getHeaderNames()) {
                log.info("Header {}: {}", headerName, response.getHeader(headerName));
            }
            
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Login successful"));
        } catch (Exception e) {
            log.error("Login failed: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Login failed: " + e.getMessage(), 400));
        }
    }

    @Operation(summary = "Register", description = "Register a new account.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                              HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request, response);
        return ResponseEntity.ok(ApiResponse.success(authResponse, "Registration successful"));
    }

    @Operation(summary = "Refresh token", description = "Refresh access token using refresh token.")
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(HttpServletRequest request,
                                                                  HttpServletResponse response) {
        log.info("Refresh token request received");
        
        String refreshToken = null;
        if (request.getCookies() != null) {
            log.info("Found {} cookies", request.getCookies().length);
            for (Cookie cookie : request.getCookies()) {
                log.info("Cookie: {} = {}", cookie.getName(), cookie.getValue() != null ? "***" : "null");
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    log.info("Found refresh token in cookie");
                    break;
                }
            }
        } else {
            log.warn("No cookies found in request");
        }

        if (refreshToken == null) {
            log.error("Refresh token not found in cookie");
            return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token not found in cookie", 400));
        }

        try {
            AuthResponse authResponse = authService.refreshToken(refreshToken, response);
            log.info("Token refreshed successfully");
            return ResponseEntity.ok(ApiResponse.success(authResponse, "Token refreshed successfully"));
        } catch (Exception e) {
            log.error("Failed to refresh token: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.error("Failed to refresh token: " + e.getMessage(), 400));
        }
    }

    @Operation(summary = "Logout", description = "Logout from one device (revoke refresh token).")
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@Valid @RequestBody RefreshTokenRequest request) {
        authService.logout(request.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.success(null, "Logout successful"));
    }

    @Operation(summary = "Logout all devices", description = "Logout from all devices (revoke all refresh tokens).")
    @PostMapping("/logout-all")
    public ResponseEntity<ApiResponse<Void>> logoutAll(Authentication authentication) {
        authService.logoutAll(authentication.getName());
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out from all devices"));
    }

    @Operation(summary = "Get current user info", description = "Get information of the currently logged-in user.")
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<AuthResponse.UserInfo>> getCurrentUser(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .lastLogin(user.getLastLogin())
                .build();

        return ResponseEntity.ok(ApiResponse.success(userInfo, "Thông tin người dùng được lấy thành công"));
    }
}
