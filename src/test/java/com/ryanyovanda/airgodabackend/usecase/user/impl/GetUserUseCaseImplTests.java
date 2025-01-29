package com.ryanyovanda.airgodabackend.usecase.user.impl;

import com.ryanyovanda.airgodabackend.common.exceptions.DataNotFoundException;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.UserDetailResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

public class GetUserUseCaseImplTests {
    @Mock
    private UsersRepository usersRepository;

    @InjectMocks
    private GetUserUseCaseImpl getUserUseCaseImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserByID_UserExists() {
        // Case 1: User found
        // Arrange
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setEmail("mail@example.com");
        user.setProfilePictureUrl("http://example.com/profile.jpg");
        user.setIsOnboardingFinished(false);

        when(usersRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDetailResponseDTO result = getUserUseCaseImpl.getUserById(userId);

        // Assert
        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getProfilePictureUrl(), result.getProfilePictureUrl());
        assertEquals(user.getIsOnboardingFinished(), result.getIsOnboardingFinished());
    }

    @Test
    void testGetUserByID_UserDoesNotExist() {
        // Case 2: User not found
        // Arrange
        Long userId = 1L;

        when(usersRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(DataNotFoundException.class, () -> getUserUseCaseImpl.getUserById(userId));
    }
}
