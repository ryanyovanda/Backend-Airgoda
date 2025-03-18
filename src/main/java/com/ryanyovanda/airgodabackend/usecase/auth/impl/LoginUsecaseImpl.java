package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.entity.Role;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.ryanyovanda.airgodabackend.common.exceptions.DataNotFoundException;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.LoginRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.TokenPairResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.RoleRepository;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.GoogleAuthUsecase;
import com.ryanyovanda.airgodabackend.usecase.auth.LoginUsecase;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenGenerationUsecase;

import lombok.extern.java.Log;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Log
@Service
public class LoginUsecaseImpl implements LoginUsecase {
  private final long ACCESS_TOKEN_EXPIRY = 900L;
  private final long REFRESH_TOKEN_EXPIRY = 86400L;

  private final AuthenticationManager authenticationManager;
  private final TokenGenerationUsecase tokenService;
  private final GoogleAuthUsecase googleAuthUsecase;
  private final UsersRepository usersRepository;
  private final RoleRepository roleRepository;

  public LoginUsecaseImpl(AuthenticationManager authenticationManager, TokenGenerationUsecase tokenService, GoogleAuthUsecase googleAuthUsecase, UsersRepository usersRepository, RoleRepository roleRepository) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
    this.googleAuthUsecase = googleAuthUsecase;
    this.usersRepository = usersRepository;
    this.roleRepository = roleRepository;
  }

  @Override
  public TokenPairResponseDTO authenticateUser(LoginRequestDTO req) {
    try {
      log.info("Loggingin with");
      log.info(req.getEmail());
      log.info(req.getPassword());
      Authentication authentication = authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
      );
      String accessToken = tokenService.generateToken(authentication, TokenGenerationUsecase.TokenType.ACCESS);
      String refreshToken = tokenService.generateToken(authentication, TokenGenerationUsecase.TokenType.REFRESH);
      return new TokenPairResponseDTO(accessToken, refreshToken, "Bearer");
    } catch (AuthenticationException e) {
      throw new DataNotFoundException("Wrong credentials");
    }
  }


  public TokenPairResponseDTO authenticateWithGoogle(String googleToken) {
    log.info("Authenticating with Google Token: " + googleToken);
    GoogleIdToken.Payload payload = googleAuthUsecase.verifyGoogleToken(googleToken);

    if (payload == null) {
      throw new DataNotFoundException("Invalid Google Token");
    }


    String email = payload.getEmail();

    Optional<User> userOptional = usersRepository.findByEmailContainsIgnoreCase(email);
    User user;

    if (userOptional.isPresent()) {
      user = userOptional.get();
    } else {
      user = new User();
      user.setEmail(email);
      user.setPassword("");
      Optional<Role> defaultRole;
      defaultRole = roleRepository.findByName("USER");
      if (defaultRole.isEmpty()) {
        throw new RuntimeException("Default role not found");
      }
      user.getRoles().add(defaultRole.get());
      usersRepository.save(user);
    }
    Set<Role> roles = user.getRoles();
    List<GrantedAuthority> authorities = roles.stream()
            .map(role -> new SimpleGrantedAuthority(role.getName()))
            .collect(Collectors.toList());

    UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(), "", authorities);

    Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());


    String accessToken = tokenService.generateToken(authentication, TokenGenerationUsecase.TokenType.ACCESS);
    String refreshToken = tokenService.generateToken(authentication, TokenGenerationUsecase.TokenType.REFRESH);

    return new TokenPairResponseDTO(accessToken, refreshToken, "Bearer");
  }
}