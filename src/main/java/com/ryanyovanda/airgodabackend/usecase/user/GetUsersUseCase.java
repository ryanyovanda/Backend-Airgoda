package com.ryanyovanda.airgodabackend.usecase.user;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.UserDetailResponseDTO;

import java.util.List;

public interface GetUsersUseCase {
  List<User> getAllUsers();
  UserDetailResponseDTO getUserById(Long id);
}
