package com.ryanyovanda.airgodabackend.infrastructure.repository;

import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;

@Repository
public class RedisTokenRepository {
    private final RedisTemplate<String, String> redisTemplate;

    public RedisTokenRepository(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveToken(String token, Duration duration) {
        try {
            if (duration == null || duration.isNegative() || duration.isZero() || duration.getSeconds() < 1) {
                System.err.println("❌ ERROR: Invalid token expiration time: " + duration + ". Using default TTL of 3600 seconds.");
                duration = Duration.ofSeconds(3600); // Set default to 1 hour if invalid
            }

            // Log token and expiration time
            System.out.println("🔹 Saving refresh token to Redis: " + token + " with TTL: " + duration.getSeconds() + " seconds");

            // Store token in Redis with validated expiration time
            redisTemplate.opsForValue().set("refresh_token:" + token, "valid", duration);

            System.out.println("✅ Token successfully saved in Redis.");
        } catch (RedisConnectionFailureException e) {
            System.err.println("❌ Redis is unavailable, cannot store token.");
        } catch (Exception e) {
            System.err.println("❌ ERROR: Failed to save token in Redis: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public boolean isTokenBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey("refresh_token:" + token));
        } catch (RedisConnectionFailureException e) {
            System.err.println("⚠️ WARNING: Redis is unavailable, assuming token is NOT blacklisted.");
            return false; // Allow tokens if Redis is down
        } catch (Exception e) {
            System.err.println("❌ ERROR: Failed to check token status: " + e.getMessage());
            return false;
        }
    }
}