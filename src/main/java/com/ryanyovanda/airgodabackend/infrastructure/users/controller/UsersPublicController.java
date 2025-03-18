package com.ryanyovanda.airgodabackend.infrastructure.users.controller;

import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.UpdateUserProfileRequestDTO;
import com.ryanyovanda.airgodabackend.usecase.auth.ResendVerificationEmailUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ryanyovanda.airgodabackend.common.response.Response;
import com.ryanyovanda.airgodabackend.infrastructure.auth.Claims;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.BulkCreateUserRequestDTO;
import com.ryanyovanda.airgodabackend.infrastructure.users.dto.CreateUserRequestDTO;
import com.ryanyovanda.airgodabackend.usecase.auth.VerifyEmailUsecase;
import com.ryanyovanda.airgodabackend.usecase.user.CreateUserUsecase;
import com.ryanyovanda.airgodabackend.usecase.user.GetUsersUseCase;
import com.ryanyovanda.airgodabackend.usecase.user.UpdateUserProfileUsecase;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;

import java.util.Map;

@Log
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UsersPublicController {

  private final GetUsersUseCase getUsersUseCase;
  private final CreateUserUsecase createUserUsecase;
  private final VerifyEmailUsecase verifyEmailUsecase;
  private final ResendVerificationEmailUsecase resendVerificationEmailUsecase;
  private final UpdateUserProfileUsecase updateUserProfileUsecase;


  @PreAuthorize("hasAuthority('SCOPE_ADMIN')")
  @GetMapping
  public ResponseEntity<?> getUsers() {
    String email = Claims.getEmailFromJwt();
    log.info("Requester email is: " + email);
    return Response.successfulResponse("Get all users success", getUsersUseCase.getAllUsers());
  }

  @GetMapping("/{id}")
  public ResponseEntity<?> getUser(@PathVariable final Long id) {
    return Response.successfulResponse("Get user details success", getUsersUseCase.getUserById(id));
  }


  @PutMapping("/profile")
  public ResponseEntity<?> updateProfile(@RequestBody UpdateUserProfileRequestDTO request) {
    String userEmail = Claims.getEmailFromJwt();
    request.setEmail(userEmail);

    User updatedUser = updateUserProfileUsecase.updateProfile(request);
    return Response.successfulResponse("Profile updated successfully", updatedUser);
  }


  @PostMapping("/register")
  public ResponseEntity<?> createUser(@RequestBody CreateUserRequestDTO req) {
    var result = createUserUsecase.createUser(req);
    return Response.successfulResponse("Create new user success", result);
  }

  @PostMapping("/bulk")
  public ResponseEntity<?> createUserBulk(@RequestBody BulkCreateUserRequestDTO req) {
    return Response.successfulResponse("Create new user success", createUserUsecase.bulkCreateUser(req));
  }

  @GetMapping("/verify")
  public ResponseEntity<?> verifyEmail(@RequestParam String token) {
    verifyEmailUsecase.verifyUser(token);
    return Response.successfulResponse("Email verified successfully! You can now log in.");
  }


  @PostMapping("/resend-verification")
  public ResponseEntity<?> resendVerificationEmail(@RequestBody Map<String, String> request) {
    String email = request.get("email");
    if (email == null || email.isEmpty()) {
      return Response.failedResponse("Email is required.");
    }

    resendVerificationEmailUsecase.resendVerificationEmail(email);
    return Response.successfulResponse("Verification email resent successfully.");
  }

}
