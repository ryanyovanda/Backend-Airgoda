package com.ryanyovanda.airgodabackend.usecase.user.impl;

import com.ryanyovanda.airgodabackend.common.exceptions.DataNotFoundException;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.UserDetailResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.user.GetUsersUseCase;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserUseCaseImpl implements GetUsersUseCase {
  private final UsersRepository usersRepository;

  public GetUserUseCaseImpl(UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  @Override
  public List<User> getAllUsers() {
    return usersRepository.findAll();
  }

  @Override
  @Cacheable(value = "userDetailResponseDTO", key = "#id", unless = "#result.isOnboardingFinished == true")
  public UserDetailResponseDTO getUserById(Long id) {
    var foundUser = usersRepository.findById(id).orElseThrow(() -> new DataNotFoundException("User not found"));
    return new UserDetailResponseDTO(foundUser.getId(), foundUser.getName(), foundUser.getEmail(), foundUser.getProfilePictureUrl(), foundUser.getIsVerified(), foundUser.getIsOnboardingFinished());
  }
}
