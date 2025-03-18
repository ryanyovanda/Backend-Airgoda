package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.VerifyEmailUsecase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifyEmailUsecaseImpl implements VerifyEmailUsecase {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public void verifyUser(String token) {
        Optional<User> userOpt = usersRepository.findByVerificationToken(token);

        if (userOpt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or expired token.");
        }

        User user = userOpt.get();

        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(OffsetDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Verification token has expired.");
        }

        user.setIsVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);

        usersRepository.save(user);
    }

}
