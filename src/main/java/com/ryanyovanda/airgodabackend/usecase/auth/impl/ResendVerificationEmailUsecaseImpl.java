package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.ResendVerificationEmailUsecase;
import com.ryanyovanda.airgodabackend.usecase.auth.SendVerificationEmailUsecase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ResendVerificationEmailUsecaseImpl implements ResendVerificationEmailUsecase {

    private final UsersRepository usersRepository;
    private final SendVerificationEmailUsecase sendVerificationEmailUsecase;

    private static final int MAX_RESEND_ATTEMPTS = 10;

    @Override
    @Transactional
    public void resendVerificationEmail(String email) {

        Optional<User> userOpt = usersRepository.findByEmailContainsIgnoreCase(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found.");
        }

        User user = userOpt.get();


        if (user.getIsVerified()) {
            throw new RuntimeException("User is already verified.");
        }


        if (user.getVerificationToken() != null && user.getVerificationToken().split("-").length > MAX_RESEND_ATTEMPTS) {
            throw new RuntimeException("You have exceeded the maximum number of resends.");
        }


        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(OffsetDateTime.now().plusHours(1));


        usersRepository.save(user);


        sendVerificationEmailUsecase.sendVerificationEmail(user.getEmail(), token);
    }
}
