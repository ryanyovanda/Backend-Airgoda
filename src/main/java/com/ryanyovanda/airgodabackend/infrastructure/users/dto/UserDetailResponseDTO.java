package com.ryanyovanda.airgodabackend.infrastructure.users.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponseDTO implements Serializable {
  private Long id;
  private String name;
  private String email;
  private String profilePictureUrl;
  private Boolean isOnboardingFinished = false;
  private Boolean isVerified;
}
