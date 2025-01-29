package com.ryanyovanda.airgodabackend.usecase.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

public interface TokenGenerationUsecase {
    enum TokenType {
    ACCESS, REFRESH
  }

  String generateToken(Authentication authentication, TokenType tokenType);
  String refreshAccessToken(String refreshToken);
}