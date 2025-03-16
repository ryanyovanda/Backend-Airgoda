package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.usecase.auth.SendVerificationEmailUsecase;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class SendVerificationEmailUsecaseImpl implements SendVerificationEmailUsecase {

    @Value("${app.frontend.url}")
    private String frontendUrl;


    private final JavaMailSender mailSender;

    public SendVerificationEmailUsecaseImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String email, String token) {
        String subject = "Airgoda - Verify Your Email";
        String verificationUrl = frontendUrl + "/verify?token=" + token;
        String content = generateEmailContent(
                "Verify Your Email Address",
                "You're almost ready to start using Airgoda. Click the button below to verify your email:",
                verificationUrl,
                "Verify My Email"
        );

        sendEmail(email, subject, content);
    }

    @Override
    public void sendResetPassword(String email, String token) {
        String subject = "Airgoda - Reset Your Password";
        String resetUrl = frontendUrl + "/reset-password?token=" + token;
        String content = generateEmailContent(
                "Reset Your Password",
                "We received a request to reset your password. Click the button below to set a new password:",
                resetUrl,
                "Reset Password"
        );

        sendEmail(email, subject, content);
    }

    private String generateEmailContent(String title, String message, String link, String buttonText) {
        return "<html><body style=\"font-family: Arial, sans-serif; text-align: center;\">"
                + "<div style=\"max-width: 500px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;\">"
                + "<img src=\"https://res.cloudinary.com/dxisnzl5i/image/upload/v1740629046/wreq8kdx9fa4tm699psg.png\" width=\"120\" alt=\"Company Logo\" style=\"margin-bottom: 20px;\"/>"
                + "<h2 style=\"color: #333;\">" + title + "</h2>"
                + "<p>" + message + "</p>"
                + "<table role=\"presentation\" cellspacing=\"0\" cellpadding=\"0\" border=\"0\" align=\"center\" style=\"margin: 20px auto;\">"
                + "<tr><td style=\"border-radius: 5px;\" bgcolor=\"#8A2DE2\">"
                + "<a href=\"" + link + "\" target=\"_blank\" style=\"background-color: #8A2DE2; color: #ffffff; padding: 12px 20px; border-radius: 5px; text-decoration: none; font-size: 16px; display: inline-block;\">"
                + buttonText + "</a>"
                + "</td></tr></table>"
                + "<p style=\"margin-top: 20px;\">Or paste this link into your browser:</p>"
                + "<p><a href=\"" + link + "\" target=\"_blank\" style=\"color: #8A2DE2; word-wrap: break-word;\">" + link + "</a></p>"
                + "<p style=\"color: #777;\">If you did not request this, please ignore this email. This link will expire in <b>one hour</b>.</p>"
                + "</div></body></html>";
    }


    private void sendEmail(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage());
        }
    }
}
