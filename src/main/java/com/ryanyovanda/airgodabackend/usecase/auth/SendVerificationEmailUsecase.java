package com.ryanyovanda.airgodabackend.usecase.auth;

public interface SendVerificationEmailUsecase {
    void sendVerificationEmail(String email, String token);
    void sendResetPassword(String email, String token);
}
