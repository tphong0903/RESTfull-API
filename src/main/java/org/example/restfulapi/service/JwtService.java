package org.example.restfulapi.service;

import org.example.restfulapi.util.TokenType;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateToken(UserDetails userDetails);
    String generateRefreshToken(UserDetails userDetails);
    String extractUsername(String token, TokenType type);
    boolean isValid(String token,UserDetails user,TokenType type);
    boolean isTokenExpired(String token,TokenType type);
}
