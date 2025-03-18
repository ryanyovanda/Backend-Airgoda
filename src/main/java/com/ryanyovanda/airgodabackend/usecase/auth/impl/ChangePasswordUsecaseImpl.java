package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.ChangePasswordUsecase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
public class ChangePasswordUsecaseImpl implements ChangePasswordUsecase {

    private static final Logger logger = LoggerFactory.getLogger(ChangePasswordUsecaseImpl.class);

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public ChangePasswordUsecaseImpl(UsersRepository usersRepository, PasswordEncoder passwordEncoder) {
        this.usersRepository = usersRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void changePassword(String token, String newPassword) {
        Optional<User> userOptional = usersRepository.findByResetToken(token);

        if (userOptional.isEmpty()) {
            logger.warn(" Invalid or non-existent reset token: {}", token);
            throw new IllegalArgumentException("Invalid reset token.");
        }

        User user = userOptional.get();

        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(OffsetDateTime.now())) {
            logger.warn(" Expired reset token for user: {}", user.getEmail());
            throw new IllegalStateException("Reset token has expired.");
        }


        String hashedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(hashedPassword);


        user.setResetToken(null);
        user.setTokenExpiry(null);

        usersRepository.save(user);
        logger.info(" Password changed successfully for user: {}", user.getEmail());
    }
}
