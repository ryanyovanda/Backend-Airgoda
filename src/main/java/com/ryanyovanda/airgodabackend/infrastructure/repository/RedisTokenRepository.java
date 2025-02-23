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
            redisTemplate.opsForValue().set(token, "blacklisted", duration);
        } catch (RedisConnectionFailureException e) {
            System.err.println("⚠️ WARNING: Redis is unavailable, cannot blacklist token.");
        } catch (Exception e) {
            System.err.println("⚠️ ERROR: Unexpected error while saving token to Redis: " + e.getMessage());
        }
    }

    public boolean isTokenBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(token));
        } catch (RedisConnectionFailureException e) {
            System.err.println("⚠️ WARNING: Redis is unavailable, assuming token is NOT blacklisted.");
            return false; // Allow tokens if Redis is down
        } catch (Exception e) {
            System.err.println("⚠️ ERROR: Unexpected error while checking token blacklist status: " + e.getMessage());
            return false;
        }
    }
}
