package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.TokenPairResponseDTO;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenGenerationUsecase;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenRefreshUsecase;
import com.ryanyovanda.airgodabackend.infrastructure.auth.utils.JwtUtil;
import org.springframework.stereotype.Service;

@Service
public class TokenRefreshUsecaseImpl implements TokenRefreshUsecase {
    private final TokenGenerationUsecase tokenService;
    private final JwtUtil jwtUtil;  // ✅ Add JwtUtil for validation

    public TokenRefreshUsecaseImpl(TokenGenerationUsecase tokenService, JwtUtil jwtUtil) {
        this.tokenService = tokenService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public TokenPairResponseDTO refreshAccessToken(String refreshToken) {
        System.out.println("Received refresh token: " + refreshToken); // 🛠 Debugging log

        if (refreshToken == null || refreshToken.isEmpty()) {
            throw new RuntimeException("Refresh token is missing");
        }

        if (!jwtUtil.validateToken(refreshToken)) {
            System.out.println("Refresh token is INVALID or EXPIRED!");
            throw new RuntimeException("Invalid or expired refresh token");
        }

        String tokenType = jwtUtil.extractAllClaims(refreshToken).get("type", String.class);
        if (!"REFRESH".equals(tokenType)) {
            System.out.println("Token is NOT a refresh token!");
            throw new RuntimeException("Invalid token type");
        }

        String newAccessToken = tokenService.refreshAccessToken(refreshToken);
        System.out.println("Generated new access token: " + newAccessToken); // 🛠 Debugging log

        return new TokenPairResponseDTO(newAccessToken, refreshToken, "Bearer");
    }

}

