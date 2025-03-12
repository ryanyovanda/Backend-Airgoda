package com.ryanyovanda.airgodabackend.infrastructure.config;

import com.ryanyovanda.airgodabackend.infrastructure.auth.filters.JwtAuthFilter;
import com.ryanyovanda.airgodabackend.infrastructure.auth.filters.TokenBlacklist;
import com.ryanyovanda.airgodabackend.usecase.auth.GetUserAuthDetailsUsecase;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import jakarta.servlet.http.Cookie;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

  @Value("${FRONTEND_URL}")
  private String frontendUrl;

  private final GetUserAuthDetailsUsecase getUserAuthDetailsUsecase;
  private final JwtConfigProperties jwtConfigProperties;
  private final PasswordEncoder passwordEncoder;
  private final TokenBlacklist tokenBlacklistFilter;
  private final JwtAuthFilter jwtAuthFilter;

  public SecurityConfig(
          GetUserAuthDetailsUsecase getUserAuthDetailsUsecase,
          JwtConfigProperties jwtConfigProperties,
          PasswordEncoder passwordEncoder,
          TokenBlacklist tokenBlacklistFilter,
          JwtAuthFilter jwtAuthFilter) {
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
            .csrf(AbstractHttpConfigurer::disable) // ❌ Disable CSRF (unless needed)
            .cors(cors -> cors.configurationSource(corsConfigurationSource())) // ✅ Apply CORS here
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/error/**").permitAll()
                    .requestMatchers("/api/v1/auth/login").permitAll()
                    .requestMatchers("/api/v1/auth/google-login").permitAll()
                    .requestMatchers("/api/v1/users/register").permitAll()
                    .requestMatchers("/api/properties/**").permitAll()
                    .requestMatchers("/api/room-variants/**").permitAll()
                    .requestMatchers("/orders/**").permitAll()
                    .requestMatchers("/peak-rates").permitAll()
                    .requestMatchers("/discounts").permitAll()
                    .requestMatchers("/api/v1/auth/refresh").permitAll()
                    .requestMatchers("/api/v1/auth/session").permitAll()
                    .requestMatchers("/api/v1/users/verify").permitAll()
                    .requestMatchers("/api/v1/users/resend-verification").permitAll()
                    .requestMatchers("/api/v1/users/**").permitAll()
                    .requestMatchers("/categories").permitAll()
                    .requestMatchers("/api/locations").permitAll()
//                    .anyRequest().authenticated()
            )
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .oauth2ResourceServer(oauth2 -> {
              oauth2.jwt(jwt -> jwt.decoder(jwtDecoder()));
              oauth2.bearerTokenResolver(request -> {
                Cookie[] cookies = request.getCookies();
                if (cookies != null) {
                  for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("SID")) {
                      return cookie.getValue();
                    }
                  }
                }
                var header = request.getHeader("Authorization");
                if (header != null) {
                  return header.replace("Bearer ", "");
                }
                return null;
              });
            })
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterAfter(tokenBlacklistFilter, BearerTokenAuthenticationFilter.class)
            .userDetailsService(getUserAuthDetailsUsecase)
            .build();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();

    // ✅ Allow frontend URL from .env + localhost
    config.setAllowedOrigins(List.of(
            frontendUrl,
            "http://localhost:3001",
            "http://localhost:3000",
            "http://0.0.0.0:3000",
            "http://host.docker.internal:3000"
    ));

    // ✅ Allow standard HTTP methods
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

    // ✅ Allow standard headers
    config.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

    // ✅ Allow cookies/auth headers
    config.setAllowCredentials(true);

    // ✅ Expose important headers
    config.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin"));

    // ✅ Apply CORS to all API routes
    source.registerCorsConfiguration("/**", config);

    return source;
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
