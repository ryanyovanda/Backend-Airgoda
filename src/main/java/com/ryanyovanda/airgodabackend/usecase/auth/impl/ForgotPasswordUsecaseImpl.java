package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.ForgotPasswordUsecase;
import com.ryanyovanda.airgodabackend.usecase.auth.SendVerificationEmailUsecase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ForgotPasswordUsecaseImpl implements ForgotPasswordUsecase {

    private final UsersRepository usersRepository;
    private final SendVerificationEmailUsecase sendVerificationEmailUsecase;

    public ForgotPasswordUsecaseImpl(UsersRepository usersRepository, SendVerificationEmailUsecase sendVerificationEmailUsecase) {
        this.usersRepository = usersRepository;
        this.sendVerificationEmailUsecase = sendVerificationEmailUsecase;
    }

    @Override
    @Transactional
    public void forgotPassword(String email) {
        Optional<User> userOptional = usersRepository.findByEmailContainsIgnoreCase(email);

        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = userOptional.get();

        if (!user.getIsVerified()) {
            throw new IllegalStateException("User email is not verified.");
        }

        String resetToken = UUID.randomUUID().toString();
        user.setResetToken(resetToken);
        user.setTokenExpiry(OffsetDateTime.now().plusHours(1));

        usersRepository.save(user);

        sendVerificationEmailUsecase.sendResetPassword(user.getEmail(), resetToken);
    }
}
