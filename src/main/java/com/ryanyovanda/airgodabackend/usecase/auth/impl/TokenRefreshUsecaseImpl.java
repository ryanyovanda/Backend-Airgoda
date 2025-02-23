package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.TokenPairResponseDTO;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenGenerationUsecase;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenRefreshUsecase;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenBlacklistUsecase;
import com.ryanyovanda.airgodabackend.infrastructure.auth.utils.JwtUtil;
import com.ryanyovanda.airgodabackend.usecase.auth.GetUserAuthDetailsUsecase;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Service
public class TokenRefreshUsecaseImpl implements TokenRefreshUsecase {
    private final TokenGenerationUsecase tokenService;
    private final TokenBlacklistUsecase tokenBlacklistUsecase;
    private final JwtUtil jwtUtil;
    private final GetUserAuthDetailsUsecase userAuthDetailsUsecase;

    public TokenRefreshUsecaseImpl(TokenGenerationUsecase tokenService, TokenBlacklistUsecase tokenBlacklistUsecase, JwtUtil jwtUtil, GetUserAuthDetailsUsecase userAuthDetailsUsecase) {
        this.tokenService = tokenService;
        this.tokenBlacklistUsecase = tokenBlacklistUsecase;
        this.jwtUtil = jwtUtil;
        this.userAuthDetailsUsecase = userAuthDetailsUsecase;
    }

    @Override
    public TokenPairResponseDTO refreshAccessToken(String refreshToken) {
        if (!jwtUtil.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid or expired refresh token");
        }

        if (tokenBlacklistUsecase.isTokenBlacklisted(refreshToken)) {
            throw new RuntimeException("Refresh token has been revoked");
        }

        String email = jwtUtil.getEmailFromToken(refreshToken);
        UserDetails userDetails = userAuthDetailsUsecase.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Set authentication in SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String newAccessToken = tokenService.generateToken(authentication, TokenGenerationUsecase.TokenType.ACCESS);
        String newRefreshToken = tokenService.generateToken(authentication, TokenGenerationUsecase.TokenType.REFRESH);

        // ✅ Fix: Format timestamp properly to ISO-8601 to prevent DateTimeParseException
        String formattedTimestamp = DateTimeFormatter.ISO_INSTANT.format(Instant.now().atOffset(ZoneOffset.UTC));
        tokenBlacklistUsecase.blacklistToken(refreshToken, formattedTimestamp);

        return new TokenPairResponseDTO(newAccessToken, newRefreshToken, "Bearer");
    }
}
