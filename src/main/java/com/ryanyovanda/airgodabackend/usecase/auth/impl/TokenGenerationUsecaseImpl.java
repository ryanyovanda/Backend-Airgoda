package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.common.exceptions.DataNotFoundException;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenGenerationUsecase;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.auth.utils.JwtUtil;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenGenerationUsecaseImpl implements TokenGenerationUsecase {
  private final JwtEncoder jwtEncoder;
  private final UsersRepository usersRepository;
  private final JwtDecoder jwtDecoder;
  private final JwtUtil jwtUtil; // ✅ Inject JwtUtil for validation

  @Value("${jwt.access.expiry}")
  private long ACCESS_TOKEN_EXPIRY;

  @Value("${jwt.refresh.expiry}")
  private long REFRESH_TOKEN_EXPIRY;

  public TokenGenerationUsecaseImpl(JwtEncoder jwtEncoder, UsersRepository usersRepository, JwtDecoder jwtDecoder, JwtUtil jwtUtil) {
    this.jwtEncoder = jwtEncoder;
    this.usersRepository = usersRepository;
    this.jwtDecoder = jwtDecoder;
    this.jwtUtil = jwtUtil; // ✅ Inject JwtUtil
  }

  @Override
  public String generateToken(Authentication authentication, TokenType tokenType) {
    Instant now = Instant.now();
    long expiry = (tokenType == TokenType.ACCESS) ? ACCESS_TOKEN_EXPIRY : REFRESH_TOKEN_EXPIRY;
    String email = authentication.getName();

    User user = usersRepository.findByEmailContainsIgnoreCase(email)
            .orElseThrow(() -> new DataNotFoundException("User not found"));

    String scope = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .reduce((a, b) -> a + " " + b)
            .orElse("");

    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(now.plusSeconds(expiry))
            .subject(email)
            .claim("scope", scope)
            .claim("userId", user.getId())
            .claim("isVerified", user.getIsVerified())
            .claim("name", user.getName() != null ? user.getName() : "Unknown")
            .claim("type", tokenType.name())
            .build();

    JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }

  @Override
  public String refreshAccessToken(String refreshToken) {
    // ✅ Use JwtUtil to validate refresh token before generating a new one
    if (!jwtUtil.validateToken(refreshToken)) {
      throw new RuntimeException("Invalid refresh token");
    }

    Jwt jwt = this.jwtDecoder.decode(refreshToken);
    Instant now = Instant.now();

    JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuedAt(now)
            .expiresAt(now.plusSeconds(ACCESS_TOKEN_EXPIRY))
            .subject(jwt.getSubject())
            .claim("scope", jwt.getClaimAsString("scope"))
            .claim("userId", jwt.getClaimAsString("userId"))
            .claim("type", TokenType.ACCESS.name())
            .build();

    JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }
}
