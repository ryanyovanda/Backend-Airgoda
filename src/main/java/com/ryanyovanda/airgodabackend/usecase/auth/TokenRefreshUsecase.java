package com.ryanyovanda.airgodabackend.usecase.auth;

import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.TokenPairResponseDTO;

public interface TokenRefreshUsecase {
    TokenPairResponseDTO refreshAccessToken(String refreshToken);
}
