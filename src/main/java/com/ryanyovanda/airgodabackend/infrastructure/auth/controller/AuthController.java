package com.ryanyovanda.airgodabackend.infrastructure.auth.controller;

import com.ryanyovanda.airgodabackend.infrastructure.auth.Claims;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.GoogleLoginRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.LoginRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.LogoutRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.auth.utils.JwtUtil;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.RefreshTokenRequestDTO;
import com.ryanyovanda.airgodabackend.common.response.Response;
import com.ryanyovanda.airgodabackend.usecase.auth.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final GoogleAuthUseCase googleAuthUseCase;
    private final LoginUsecase loginUsecase;
    private final TokenRefreshUsecase tokenRefreshUsecase;
    private final LogoutUsecase logoutUsecase;

    public AuthController(GoogleAuthUseCase googleAuthUseCase, LoginUsecase loginUsecase, TokenRefreshUsecase tokenRefreshUsecase,
                          TokenBlacklistUsecase tokenBlacklistUsecase, LogoutUsecase logoutUsecase) {
        this.loginUsecase = loginUsecase;
        this.tokenRefreshUsecase = tokenRefreshUsecase;
        this.logoutUsecase = logoutUsecase;
        this.googleAuthUseCase = googleAuthUseCase;

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Validated @RequestBody LoginRequestDTO req) {
        return Response.successfulResponse("Login successful", loginUsecase.authenticateUser(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@Validated @RequestBody LogoutRequestDTO req) {
        var accessToken = Claims.getJwtTokenString();
        req.setAccessToken(accessToken);
        return Response.successfulResponse("Logout successful", logoutUsecase.logoutUser(req));
    }

//    @PostMapping("/refresh")
//    public ResponseEntity<?> refresh() {
//        String tokenType = Claims.getTokenTypeFromJwt();
//        if (!"REFRESH".equals(tokenType)) {
//            return Response.failedResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid token type for refresh");
//        }
//        String token = Claims.getJwtTokenString();
//        return Response.successfulResponse("Refresh successful", tokenRefreshUsecase.refreshAccessToken(token));
//    }


    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@Validated @RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();
        if (!JwtUtil.validateToken(refreshToken)) {
            return Response.failedResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token");
        }

        String email = JwtUtil.getEmailFromToken(refreshToken);
        String newAccessToken = JwtUtil.generateToken(email, false); // ✅ Fix applied

        return Response.successfulResponse("Token refreshed", Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> loginWithGoogle(@Validated @RequestBody GoogleLoginRequestDTO request) {
        String idToken = request.getIdToken();

        if (idToken == null || idToken.isEmpty()) {
            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Missing ID Token");
        }

        try {
            // ✅ Verify Google Token & Generate JWT
            Map<String, Object> userDetails = googleAuthUseCase.verifyToken(idToken);
            return Response.successfulResponse("Google Login Successful", userDetails);
        } catch (GeneralSecurityException | IOException e) {
            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Google ID Token");
        }
    }
}




