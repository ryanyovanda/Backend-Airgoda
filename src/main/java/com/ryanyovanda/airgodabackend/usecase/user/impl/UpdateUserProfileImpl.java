package com.ryanyovanda.airgodabackend.usecase.user.impl;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.UpdateUserProfileRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.user.UpdateUserProfileUsecase;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UpdateUserProfileImpl implements UpdateUserProfileUsecase {
    private final UsersRepository usersRepository;

    @Override
    @Transactional
    public User updateProfile(UpdateUserProfileRequestDTO req){
        Optional<User> optionalUser = usersRepository.findByEmailContainsIgnoreCase(req.getEmail());
        if (optionalUser.isEmpty()){
            throw new IllegalStateException("User not found");
        }
        User user = optionalUser.get();
        user.setName(req.getName());
        user.setProfilePictureUrl(req.getProfilePictureUrl());
        return usersRepository.save(user);
    }
}
