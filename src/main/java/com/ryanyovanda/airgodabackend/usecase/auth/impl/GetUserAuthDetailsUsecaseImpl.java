package com.ryanyovanda.airgodabackend.usecase.auth.impl;

import com.ryanyovanda.airgodabackend.common.exceptions.DataNotFoundException;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.auth.dto.UserAuth;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.auth.GetUserAuthDetailsUsecase;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class GetUserAuthDetailsUsecaseImpl implements GetUserAuthDetailsUsecase {
  private final UsersRepository usersRepository;

  public GetUserAuthDetailsUsecaseImpl(UsersRepository usersRepository) {
    this.usersRepository = usersRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User existingUser = usersRepository.findByEmailContainsIgnoreCase(username).orElseThrow(() -> new DataNotFoundException("User not found with email: " + username));

    UserAuth userAuth = new UserAuth();
    userAuth.setUser(existingUser);
    return userAuth;
  }
}
