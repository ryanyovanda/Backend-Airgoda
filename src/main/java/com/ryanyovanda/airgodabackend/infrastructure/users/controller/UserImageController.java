package com.ryanyovanda.airgodabackend.infrastructure.users.controller;

import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.user.UploadUserImageUsecase;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
@RestController
@RequestMapping("/api/v1/users/{userId}/profile-image")
public class UserImageController {

    private final UploadUserImageUsecase uploadUserImageUsecase;
    private final UsersRepository usersRepository;

    public UserImageController(UploadUserImageUsecase uploadUserImageUsecase, UsersRepository usersRepository) {
        this.uploadUserImageUsecase = uploadUserImageUsecase;
        this.usersRepository = usersRepository;
    }

    
    private boolean isAuthorized(Long userId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String authenticatedEmail = jwt.getClaim("sub");
            return usersRepository.findById(userId)
                    .map(user -> user.getEmail().equals(authenticatedEmail))
                    .orElse(false);
        }
        return false;
    }


    @GetMapping
    public ResponseEntity<String> getUserImage(@PathVariable Long userId) {
        return usersRepository.findById(userId)
                .map(user -> ResponseEntity.ok(user.getImageUrl()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<String> uploadUserImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        if (!isAuthorized(userId)) {
            return ResponseEntity.status(403).body("Unauthorized to update this profile image.");
        }

        String imageUrl = uploadUserImageUsecase.uploadUserImage(userId, file);
        return ResponseEntity.ok(imageUrl);
    }

    @PutMapping
    public ResponseEntity<String> updateUserImage(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
        if (!isAuthorized(userId)) {
            return ResponseEntity.status(403).body("Unauthorized to update this profile image.");
        }

        String imageUrl = uploadUserImageUsecase.uploadUserImage(userId, file);
        return ResponseEntity.ok(imageUrl);
    }

    @DeleteMapping
    public ResponseEntity<String> deleteUserImage(@PathVariable Long userId) {
        if (!isAuthorized(userId)) {
            return ResponseEntity.status(403).body("Unauthorized to delete this profile image.");
        }

        uploadUserImageUsecase.deleteUserImage(userId);
        return ResponseEntity.ok("Profile image deleted successfully.");
    }
}
