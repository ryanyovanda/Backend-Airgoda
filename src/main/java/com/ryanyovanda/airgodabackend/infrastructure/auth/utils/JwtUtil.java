package com.ryanyovanda.airgodabackend.infrastructure.auth.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // ✅ Secret key for signing JWT (should be stored securely, e.g., application.properties)
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long ACCESS_TOKEN_EXPIRY = 1000 * 60 * 30; // 30 minutes
    private static final long REFRESH_TOKEN_EXPIRY = 1000 * 60 * 60 * 24 * 7; // 7 days

    // ✅ Generate JWT Token (access or refresh)
    public static String generateToken(String email, boolean isRefreshToken) {
        long expiry = isRefreshToken ? REFRESH_TOKEN_EXPIRY : ACCESS_TOKEN_EXPIRY;

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .signWith(SECRET_KEY)
                .compact();
    }

    // ✅ Extract email from JWT
    public static String getEmailFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // ✅ Validate JWT Token
    public static boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
