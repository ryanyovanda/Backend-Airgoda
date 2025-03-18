package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.VerifyResetTokenUsecase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class VerifyResetTokenUsecaseImpl implements VerifyResetTokenUsecase {

    private static final Logger logger = LoggerFactory.getLogger(VerifyResetTokenUsecaseImpl.class);

    private final UsersRepository usersRepository;

    public VerifyResetTokenUsecaseImpl(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public boolean verifyResetToken(String token) {
        Optional<User> userOptional = usersRepository.findByResetToken(token);

        if (userOptional.isEmpty()) {
            logger.warn(" Invalid or non-existent reset token: {}", token);
            return false;
        }

        User user = userOptional.get();

        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(OffsetDateTime.now())) {
            logger.warn(" Expired reset token for user: {}", user.getEmail());
            return false;
        }

        logger.info(" Valid reset token verified for user: {}", user.getEmail());
        return true;
    }
}
