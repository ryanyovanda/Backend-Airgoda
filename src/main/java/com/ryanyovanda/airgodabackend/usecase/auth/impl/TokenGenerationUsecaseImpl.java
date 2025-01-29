package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.common.exceptions.DataNotFoundException;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenGenerationUsecase;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenGenerationUsecaseImpl implements TokenGenerationUsecase {
  private final JwtEncoder jwtEncoder;
  private final UsersRepository usersRepository;
  private final JwtDecoder jwtDecoder;

  private final long ACCESS_TOKEN_EXPIRY = 900L; // 15 minutes
  private final long REFRESH_TOKEN_EXPIRY = 86400L; // 24 hours

  public TokenGenerationUsecaseImpl(JwtEncoder jwtEncoder, UsersRepository usersRepository, JwtDecoder jwtDecoder) {
    this.jwtEncoder = jwtEncoder;
    this.usersRepository = usersRepository;
    this.jwtDecoder = jwtDecoder;
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
        .claim("type", tokenType.name())
        .build();

    JwsHeader jwsHeader = JwsHeader.with(() -> "HS256").build();
    return this.jwtEncoder.encode(JwtEncoderParameters.from(jwsHeader, claims)).getTokenValue();
  }

  @Override
  public String refreshAccessToken(String refreshToken) {
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