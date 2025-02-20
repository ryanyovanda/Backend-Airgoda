package com.ryanyovanda.airgodabackend.infrastructure.auth.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access.expiry}")
    private long accessTokenExpiry;

    @Value("${jwt.refresh.expiry}")
    private long refreshTokenExpiry;

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email, boolean isRefreshToken) {
        long expiry = isRefreshToken ? refreshTokenExpiry * 1000 : accessTokenExpiry * 1000;

        return Jwts.builder()
                .setSubject(email)
                .setIssuer("Airgoda") // ✅ Added issuer
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiry))
                .claim("type", isRefreshToken ? "refresh" : "access") // ✅ Added token type
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey()) // ✅ Fixed deprecated method
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getEmailFromToken(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean validateToken(String token) {
        try {
            System.out.println("Validating token: " + token);

            Jwts.parser()
                    .setSigningKey(getSigningKey()) // 🛠️ This might be the problem
                    .build()
                    .parseClaimsJws(token);

            System.out.println("Token is valid!");
            return true;
        } catch (Exception e) {
            System.out.println("JWT Validation failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

}

