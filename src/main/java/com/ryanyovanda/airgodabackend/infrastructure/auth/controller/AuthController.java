package com.ryanyovanda.airgodabackend.infrastructure.auth.controller;

import com.ryanyovanda.airgodabackend.infrastructure.auth.Claims;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.*;
import com.ryanyovanda.airgodabackend.infrastructure.auth.utils.JwtUtil;
import com.ryanyovanda.airgodabackend.common.response.Response;
import com.ryanyovanda.airgodabackend.usecase.auth.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final GetUserAuthDetailsUsecase userAuthDetailsUsecase;
    private final JwtUtil jwtUtil;

    public AuthController(
            GoogleAuthUsecase googleAuthUsecase,
            LoginUsecase loginUsecase,
            TokenRefreshUsecase tokenRefreshUsecase,
            LogoutUsecase logoutUsecase,
            GetUserAuthDetailsUsecase userAuthDetailsUsecase,
            JwtUtil jwtUtil
    ) {
        this.googleAuthUsecase = googleAuthUsecase;
        this.loginUsecase = loginUsecase;
        this.tokenRefreshUsecase = tokenRefreshUsecase;
        this.logoutUsecase = logoutUsecase;
        this.userAuthDetailsUsecase = userAuthDetailsUsecase;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO req) {
        return Response.successfulResponse("Login successful", loginUsecase.authenticateUser(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LogoutRequestDTO req) {
        var accessToken = Claims.getJwtTokenString();
        req.setAccessToken(accessToken);
        return Response.successfulResponse("Logout successful", logoutUsecase.logoutUser(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshAccessToken(@RequestBody RefreshTokenRequestDTO request) {
        String refreshToken = request.getRefreshToken();

        // ✅ Validate the refresh token
        if (!jwtUtil.validateToken(refreshToken)) {
            return Response.failedResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid or expired refresh token");
        }

        // ✅ Extract email from refresh token
        String email = jwtUtil.getEmailFromToken(refreshToken);

        // ✅ Load user details and manually authenticate the refresh token
        UserDetails userDetails = userAuthDetailsUsecase.loadUserByUsername(email);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ Call the use case to generate new tokens
        TokenPairResponseDTO newTokens = tokenRefreshUsecase.refreshAccessToken(refreshToken);

        return Response.successfulResponse("Refresh successful", newTokens);
    }

    @PostMapping("/google-login")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> requestBody) {
        log.info(" Google login request received: " + requestBody.toString());
        String googleToken = requestBody.get("token");
        return Response.successfulResponse("Login successful", loginUsecase.authenticateWithGoogle(googleToken));
    }
}
