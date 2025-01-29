package com.ryanyovanda.airgodabackend.usecase.user.impl;

import com.ryanyovanda.airgodabackend.entity.Role;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.CreateUserRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.UserDetailResponseDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.RoleRepository;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class CreateUserUsecaseImplTests {
    @Mock
    UsersRepository usersRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    RoleRepository roleRepository;

    @InjectMocks
    CreateUserUsecaseImpl createUserUsecase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateUser_Success() {
        // Case 1: User created successfully
        // Arrange
        CreateUserRequestDTO req = new CreateUserRequestDTO();
        req.setEmail("test@example.com");
        req.setPassword("password");
        req.setPin("1234");
        req.setProfilePictureUrl("http://example.com/profile.jpg");

        User newUser = req.toEntity();

        Role role   = new Role();
        role.setName("USER");

        // Mocking the behavior of the dependencies
        User userToBeInserted = req.toEntity();
        userToBeInserted.setPassword("encodedPassword");
        userToBeInserted.getRoles().add(role);

        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(java.util.Optional.of(role));
        when(usersRepository.save(any(User.class))).thenReturn(userToBeInserted);

        // Act
        UserDetailResponseDTO result = createUserUsecase.createUser(req);

        // Assert
        assertNotNull(result);
        assertEquals(newUser.getEmail(), result.getEmail());
        assertEquals(newUser.getProfilePictureUrl(), result.getProfilePictureUrl());
        assertEquals(newUser.getIsOnboardingFinished(), result.getIsOnboardingFinished());
    }

    @Test
    void testCreateUser_DefaultRoleNotFound() {
        // Case 2: Default role not found
        // Arrange
        CreateUserRequestDTO req = new CreateUserRequestDTO();
        req.setEmail("test@example.com");
        req.setPassword("password");
        req.setPin("1234");
        req.setProfilePictureUrl("http://example.com/profile.jpg");

        User newUser = req.toEntity();
        when(passwordEncoder.encode(newUser.getPassword())).thenReturn("encodedPassword");
        when(roleRepository.findByName("USER")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exceptions = assertThrows(RuntimeException.class, () -> createUserUsecase.createUser(req));
        assertEquals("Default role not found", exceptions.getMessage());
    }
}
