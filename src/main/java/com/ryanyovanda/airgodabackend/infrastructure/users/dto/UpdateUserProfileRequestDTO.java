package com.ryanyovanda.airgodabackend.infrastructure.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserProfileRequestDTO {
    private String name;
    private String email;  // If other profile fields are editable
    private String profilePictureUrl;
}
