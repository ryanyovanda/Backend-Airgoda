package com.ryanyovanda.airgodabackend.usecase.user;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.UpdateUserProfileRequestDTO;

public interface UpdateUserProfileUsecase {
    User updateProfile(UpdateUserProfileRequestDTO req);
}
