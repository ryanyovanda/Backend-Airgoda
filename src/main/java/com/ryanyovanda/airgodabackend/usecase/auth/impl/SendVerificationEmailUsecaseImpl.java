package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.SendVerificationEmailUsecase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SendVerificationEmailUsecaseImpl implements SendVerificationEmailUsecase {

    private final UsersRepository usersRepository;
    private final JavaMailSender mailSender;

    @Override
    @Transactional
    public void sendVerificationEmail(String email) {
        // ✅ Fetch user from the database
        Optional<User> userOpt = usersRepository.findByEmailContainsIgnoreCase(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found with email: " + email);
        }

        User user = userOpt.get();

        // ✅ Generate new token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(OffsetDateTime.now().plusHours(1));

        // ✅ Save user with updated token
        usersRepository.save(user);

        // ✅ Send email
        sendEmail(user.getEmail(), token);
    }

    private void sendEmail(String to, String token) {
        String subject = "Verify Your Email";
        String verificationLink = "http://localhost:3000/api/v1/users/verify?token=" + token;
        String body = "<p>Click the link below to verify your email:</p>"
                + "<a href=\"" + verificationLink + "\">Verify Email</a>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
