package com.bacthinh.BacThinh.service;

import com.bacthinh.BacThinh.entity.User;

import java.util.Date;

public interface JwtService {
    String generateAccessToken(User user);
    String generateRefreshToken(User user);
    boolean validateToken(String token);
    String getEmailFromToken(String token);
    Long getUserIdFromToken(String token);
    Date getExpirationFromToken(String token);
}
