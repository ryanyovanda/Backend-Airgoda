package com.ryanyovanda.airgodabackend.infrastructure.users.dto;

import java.util.HashSet;
import java.util.Set;

import com.ryanyovanda.airgodabackend.entity.Role;
import com.ryanyovanda.airgodabackend.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequestDTO {
  private String email;
  private String password;
  private String pin;
  private String profilePictureUrl;

  public User toEntity() {
    User user = new User();
    user.setEmail(email);
    user.setPassword(password);
    user.setPin(pin);
    user.setProfilePictureUrl(profilePictureUrl);
    user.setIsOnboardingFinished(false);
    Set<Role> roles = new HashSet<>();
    user.setRoles(roles);
    return user;
  }
}
