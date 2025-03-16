package com.ryanyovanda.airgodabackend.usecase.auth;

public interface ChangePasswordUsecase {
    void changePassword(String token, String newPassword);
}
