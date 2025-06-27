package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.dto.request.LoginRequest;
import com.bacthinh.BacThinh.dto.request.RegisterRequest;
import com.bacthinh.BacThinh.dto.response.AuthResponse;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request, HttpServletResponse response);

    AuthResponse register(RegisterRequest request, HttpServletResponse response);

    AuthResponse refreshToken(String oldRefreshToken, HttpServletResponse response);

    void logout(String refreshToken);

    void logoutAll(String email);
}

