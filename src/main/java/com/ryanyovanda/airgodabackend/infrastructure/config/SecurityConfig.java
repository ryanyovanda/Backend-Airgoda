package com.ryanyovanda.airgodabackend.infrastructure.config;

import com.ryanyovanda.airgodabackend.infrastructure.auth.filters.JwtAuthFilter;
import com.ryanyovanda.airgodabackend.infrastructure.auth.filters.TokenBlacklist;
import com.ryanyovanda.airgodabackend.usecase.auth.GetUserAuthDetailsUsecase;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
  private final GetUserAuthDetailsUsecase getUserAuthDetailsUsecase;
  private final JwtConfigProperties jwtConfigProperties;
  private final PasswordEncoder passwordEncoder;
  private final TokenBlacklist tokenBlacklistFilter;
  private final JwtAuthFilter jwtAuthFilter; // ✅ Add JWT Authentication Filter

  public SecurityConfig(
          GetUserAuthDetailsUsecase getUserAuthDetailsUsecase,
          JwtConfigProperties jwtConfigProperties,
          PasswordEncoder passwordEncoder,
          TokenBlacklist tokenBlacklistFilter,
          JwtAuthFilter jwtAuthFilter) { // ✅ Inject JWT Filter
    this.getUserAuthDetailsUsecase = getUserAuthDetailsUsecase;
    this.jwtConfigProperties = jwtConfigProperties;
    this.passwordEncoder = passwordEncoder;
    this.tokenBlacklistFilter = tokenBlacklistFilter;
    this.jwtAuthFilter = jwtAuthFilter;
  }

  @Bean
  public AuthenticationManager authManager() {
    DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
    authProvider.setUserDetailsService(getUserAuthDetailsUsecase);
    authProvider.setPasswordEncoder(passwordEncoder);
    return new ProviderManager(authProvider);
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    return http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(new CorsConfigurationSourceImpl()))
            .authorizeHttpRequests(auth -> auth
                    // ✅ Define public routes
                    .requestMatchers("/error/**").permitAll()
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    .requestMatchers("/api/v1/auth/google-login").permitAll() // ✅ Allow Google Login
                    .requestMatchers("/api/v1/users/register").permitAll()


                    // ✅ Define protected routes (JWT required)
                    .requestMatchers("/api/v1/protected-resource/**").authenticated()
                    .anyRequest().authenticated())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> {
              oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())); // ✅ Decode JWT
              oauth2.bearerTokenResolver(request -> {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                  for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("SID")) {
                      return cookie.getValue();
                    }
                  }
                }

                // ✅ Get from headers instead of cookies
                var header = request.getHeader("Authorization");
                if (header != null) {
                  return header.replace("Bearer ", "");
                }

                return null;
              });
            })
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) // ✅ Add JWT Filter
            .addFilterAfter(tokenBlacklistFilter, BearerTokenAuthenticationFilter.class)
            .userDetailsService(getUserAuthDetailsUsecase)
            .build();
  }

  @Bean
  public JwtDecoder jwtDecoder() {
    SecretKey originalKey = new SecretKeySpec(jwtConfigProperties.getSecret().getBytes(), "HmacSHA256");
    return NimbusJwtDecoder.withSecretKey(originalKey).build();
  }

  @Bean
  public JwtEncoder jwtEncoder() {
    SecretKey key = new SecretKeySpec(jwtConfigProperties.getSecret().getBytes(), "HmacSHA256");
    JWKSource<SecurityContext> immutableSecret = new ImmutableSecret<>(key);
    return new NimbusJwtEncoder(immutableSecret);
  }
}
