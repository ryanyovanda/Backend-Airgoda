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
                duration = Duration.ofSeconds(3600);
            }

            redisTemplate.opsForValue().set("refresh_token:" + token, "valid", duration);
        } catch (RedisConnectionFailureException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public boolean isTokenBlacklisted(String token) {
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey("refresh_token:" + token));
        } catch (RedisConnectionFailureException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}