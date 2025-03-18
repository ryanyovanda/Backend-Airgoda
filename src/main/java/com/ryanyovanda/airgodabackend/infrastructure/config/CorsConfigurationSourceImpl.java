package com.ryanyovanda.airgodabackend.infrastructure.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import org.springframework.context.annotation.Bean;

@Configuration
public class CorsConfigurationSourceImpl implements CorsConfigurationSource {

  @Value("${FRONTEND_URL}")
  private String frontendUrl;

  @Override
  public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
    CorsConfiguration corsConfiguration = new CorsConfiguration();

    corsConfiguration.setAllowedOrigins(List.of(
            "http://localhost:3001",
            "http://localhost:3000",
            "http://0.0.0.0:3000",
            "http://host.docker.internal:3000",
            frontendUrl
    ));

    corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));

    corsConfiguration.setAllowCredentials(true);

    corsConfiguration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));

    corsConfiguration.setExposedHeaders(List.of("Authorization", "Access-Control-Allow-Origin"));

    return corsConfiguration;
  }

}
