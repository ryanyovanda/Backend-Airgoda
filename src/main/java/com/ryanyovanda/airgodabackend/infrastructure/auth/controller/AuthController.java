package com.ryanyovanda.airgodabackend.infrastructure.auth.controller;

import com.ryanyovanda.airgodabackend.infrastructure.auth.Claims;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.*;
import com.ryanyovanda.airgodabackend.infrastructure.auth.utils.JwtUtil;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.RefreshTokenRequestDTO;
import com.ryanyovanda.airgodabackend.common.response.Response;
import com.ryanyovanda.airgodabackend.usecase.auth.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
    private final GoogleAuthUsecase googleAuthUsecase;
    private final LoginUsecase loginUsecase;
    private final TokenRefreshUsecase tokenRefreshUsecase;
    private final LogoutUsecase logoutUsecase;

    public AuthController(GoogleAuthUsecase googleAuthUsecase, LoginUsecase loginUsecase, TokenRefreshUsecase tokenRefreshUsecase,
                          TokenBlacklistUsecase tokenBlacklistUsecase, LogoutUsecase logoutUsecase) {
        this.loginUsecase = loginUsecase;
        this.tokenRefreshUsecase = tokenRefreshUsecase;
        this.logoutUsecase = logoutUsecase;
        this.googleAuthUsecase = googleAuthUsecase;

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
        public ResponseEntity<?> refresh() {
            String tokenType = Claims.getTokenTypeFromJwt();
            if (!"REFRESH".equals(tokenType)) {
                return Response.failedResponse("Invalid refresh token");
            }
            String token = Claims.getJwtTokenString();
            return Response.successfulResponse("Refresh successful", tokenRefreshUsecase.refreshAccessToken(token));
        }

//    @PostMapping("/refresh")
//    public ResponseEntity<?> refreshAccessToken(@Validated @RequestBody RefreshTokenRequestDTO request) {
//        String refreshToken = request.getRefreshToken();
//        if (!JwtUtil.validateToken(refreshToken)) {
//            return Response.failedResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token");
//        }
//
//        String email = JwtUtil.getEmailFromToken(refreshToken);
//        String newAccessToken = JwtUtil.generateToken(email, false); // ✅ Fix applied
//
//        return Response.successfulResponse("Token refreshed", Map.of("accessToken", newAccessToken));
//    }
    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> requestBody) {
        log.info(" Google login request received: " + requestBody.toString());
        String googleToken = requestBody.get("token");
        return Response.successfulResponse("Login successful", loginUsecase.authenticateWithGoogle(googleToken));
    }
//
//    @PostMapping("/google-login")
//    public ResponseEntity<?> loginWithGoogle(@Validated @RequestBody GoogleLoginRequestDTO request) {
//        String idToken = request.getIdToken();
//
//        if (idToken == null || idToken.isEmpty()) {
//            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Missing ID Token");
//        }
//
//        try {
//            // ✅ Verify Google Token & Generate JWT
//            Map<String, Object> userDetails = googleAuthUseCase.verifyToken(idToken);
//            return Response.successfulResponse("Google Login Successful", userDetails);
//        } catch (GeneralSecurityException | IOException e) {
//            return Response.failedResponse(HttpStatus.BAD_REQUEST.value(), "Invalid Google ID Token");
//        }
//    }
}




