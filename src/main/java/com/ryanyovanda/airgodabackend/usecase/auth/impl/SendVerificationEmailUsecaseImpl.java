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
        Optional<User> userOpt = usersRepository.findByEmailContainsIgnoreCase(email);

        if (userOpt.isEmpty()) {
            System.out.println("❌ User not found with email: " + email);
            throw new RuntimeException("User not found with email: " + email);
        }

        User user = userOpt.get();

        // ✅ Generate and store a new verification token
        String token = UUID.randomUUID().toString();
        user.setVerificationToken(token);
        user.setTokenExpiry(OffsetDateTime.now().plusHours(1));

        usersRepository.save(user);  // ✅ Ensure token is saved in DB
        System.out.println("✅ Token generated and saved: " + token);

        // ✅ Confirm token persistence by re-fetching from DB
        Optional<User> verifySave = usersRepository.findByEmailContainsIgnoreCase(email);
        if (verifySave.isEmpty() || verifySave.get().getVerificationToken() == null) {
            System.out.println("❌ Token failed to save in database!");
            throw new RuntimeException("Failed to save verification token.");
        }

        // ✅ Send verification email
        sendEmail(user.getEmail(), token);
    }

    private void sendEmail(String to, String token) {
        String subject = "Verify Your Email";
        String verificationLink = "http://localhost:3000/verify?token=" + token;

        String body = "<html><body style=\"font-family: Arial, sans-serif; text-align: center;\">"
                + "<div style=\"max-width: 500px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;\">"
                + "<img src=\"https://res.cloudinary.com/dxisnzl5i/image/upload/v1740629046/wreq8kdx9fa4tm699psg.png\" width=\"120\" alt=\"Company Logo\" style=\"margin-bottom: 20px;\"/>"
                + "<h2 style=\"color: #333;\">Verify Your Email Address</h2>"
                + "<p>Please confirm that you want to use this email to activate your account.</p>"
                + "<p>Once verified, you'll be able to access all features.</p>"
                + "<a href=\"" + verificationLink + "\" style=\"background-color: #8A2DE2; color: #ffffff; padding: 12px 20px; border-radius: 5px; text-decoration: none; font-size: 16px; display: inline-block;\">Verify My Email</a>"
                + "<p style=\"margin-top: 20px;\">Or paste this link into your browser:</p>"
                + "<p><a href=\"" + verificationLink + "\" style=\"color: #8A2DE2;\">" + verificationLink + "</a></p>"
                + "</div></body></html>";

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
            System.out.println("✅ Verification email sent successfully to: " + to);
        } catch (MessagingException e) {
            System.out.println("❌ Email sending failed!");
            throw new RuntimeException("Failed to send email", e);
        }
    }
}
