package com.ryanyovanda.airgodabackend.usecase.user;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.BulkCreateUserRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.CreateUserRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.UserDetailResponseDTO;

import java.util.List;

public interface CreateUserUsecase {
  UserDetailResponseDTO createUser(CreateUserRequestDTO req);
  User createUserWithEntity(User req);
  List<User> bulkCreateUser(BulkCreateUserRequestDTO req);
}
