package com.ryanyovanda.airgodabackend.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfigurationSourceImpl implements CorsConfigurationSource {

  @Value("${FRONTEND_URL}")
  private String frontendUrl;

  @Override
  public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
    CorsConfiguration corsConfiguration = new CorsConfiguration();

    // Allowed frontend origins (use ENV variable)
    corsConfiguration.setAllowedOriginPatterns(Arrays.asList(
            "http://localhost:3001",
            "http://localhost:3000",
            "http://0.0.0.0:3000",
            "http://host.docker.internal:3000",
            frontendUrl // ✅ Fetch frontend URL from environment variable
    ));

    // Allowed HTTP methods
    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

    // Allow credentials (cookies, authorization headers, etc.)
    corsConfiguration.setAllowCredentials(true);

    // Allowed headers
    corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

    // Exposed headers
    corsConfiguration.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin"));

    return corsConfiguration;
  }
}
