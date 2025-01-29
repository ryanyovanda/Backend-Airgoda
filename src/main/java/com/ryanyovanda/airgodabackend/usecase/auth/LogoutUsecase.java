package com.ryanyovanda.airgodabackend.usecase.auth;

import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.LogoutRequestDTO;

public interface LogoutUsecase {
    Boolean logoutUser(LogoutRequestDTO req);
}
