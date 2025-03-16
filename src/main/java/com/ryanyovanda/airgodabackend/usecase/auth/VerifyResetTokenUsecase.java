package com.ryanyovanda.airgodabackend.usecase.auth;

public interface VerifyResetTokenUsecase {
    boolean verifyResetToken(String token);
}
