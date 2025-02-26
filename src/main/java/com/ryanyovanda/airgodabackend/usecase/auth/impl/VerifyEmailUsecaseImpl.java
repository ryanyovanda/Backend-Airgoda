package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.VerifyEmailUsecase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class VerifyEmailUsecaseImpl implements VerifyEmailUsecase {

    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public void verifyUser(String token) {
        // ✅ Find user by verification token
        Optional<User> userOpt = usersRepository.findByVerificationToken(token);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("Invalid or expired token.");
        }

        User user = userOpt.get();

        // ✅ Check if token has expired
        if (user.getTokenExpiry() == null || user.getTokenExpiry().isBefore(OffsetDateTime.now())) {
            throw new RuntimeException("Verification token has expired.");
        }

        // ✅ Mark user as verified
        user.setIsVerified(true);
        user.setVerificationToken(null);
        user.setTokenExpiry(null);

        // ✅ Save changes
        usersRepository.save(user);
    }
}
