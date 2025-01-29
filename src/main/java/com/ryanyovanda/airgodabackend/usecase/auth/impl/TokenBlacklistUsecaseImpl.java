package com.ryanyovanda.airgodabackend.usecase.auth.impl;


import com.ryanyovanda.airgodabackend.infrastructure.repository.RedisTokenRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.TokenBlacklistUsecase;

import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TokenBlacklistUsecaseImpl implements TokenBlacklistUsecase {
    private final String REDIS_BLACKLIST_KEY = "fezz4ubackend_blacklist_token:";
    private final RedisTokenRepository redisTokenRepository;

    public TokenBlacklistUsecaseImpl(RedisTokenRepository redisTokenRepository) {
        this.redisTokenRepository = redisTokenRepository;
    }

    @Override
    public void blacklistToken(String token, String expiredAt) {
        Duration duration = Duration.between(java.time.Instant.now(), java.time.Instant.parse(expiredAt));
        redisTokenRepository.saveToken(REDIS_BLACKLIST_KEY + token, duration);
    }

    @Override
    public boolean isTokenBlacklisted(String token) {
        return redisTokenRepository.isTokenBlacklisted(REDIS_BLACKLIST_KEY + token);
    }
}
