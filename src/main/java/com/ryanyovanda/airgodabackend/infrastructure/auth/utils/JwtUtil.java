package com.ryanyovanda.airgodabackend.infrastructure.auth.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(String email, boolean isRefreshToken) {
        long expiryDuration = isRefreshToken ? refreshTokenExpiry : accessTokenExpiry;
        Date issuedAt = Date.from(Instant.now());
        Date expiryDate = Date.from(Instant.now().plus(expiryDuration, ChronoUnit.SECONDS));

        return Jwts.builder()
                .setSubject(email)
                .setIssuer("Airgoda")
                .setIssuedAt(issuedAt)
                .setExpiration(expiryDate)
                .claim("type", isRefreshToken ? "refresh" : "access")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
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
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            System.out.println("Token is valid!");
            return true;
        } catch (ExpiredJwtException e) {
            System.out.println("JWT expired: " + e.getMessage());
        } catch (UnsupportedJwtException e) {
            System.out.println("Unsupported JWT: " + e.getMessage());
        } catch (MalformedJwtException e) {
            System.out.println("Malformed JWT: " + e.getMessage());
        } catch (SignatureException e) {
            System.out.println("Invalid signature: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("JWT validation failed: " + e.getMessage());
        }
        return false;
    }
}
