package com.ryanyovanda.airgodabackend.usecase.auth;

import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.LoginRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.TokenPairResponseDTO;

public interface LoginUsecase {
  TokenPairResponseDTO authenticateUser(LoginRequestDTO req);
}