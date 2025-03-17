package com.ryanyovanda.airgodabackend.usecase.user.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.ryanyovanda.airgodabackend.entity.User;
import com.ryanyovanda.airgodabackend.infrastructure.users.repository.UsersRepository;
import com.ryanyovanda.airgodabackend.usecase.user.UploadUserImageUsecase;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UploadUserImageUsecaseImpl implements UploadUserImageUsecase {

    private final UsersRepository usersRepository;
    private final Cloudinary cloudinary;

    private static final List<String> ALLOWED_FORMATS = Arrays.asList("image/jpeg", "image/png", "image/gif");

    public UploadUserImageUsecaseImpl(UsersRepository usersRepository, @Value("${CLOUDINARY_URL}") String cloudinaryUrl) {
        this.usersRepository = usersRepository;
        this.cloudinary = new Cloudinary(cloudinaryUrl);
    }

    @Override
    @Transactional
    public String uploadUserImage(Long userId, MultipartFile file) {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty.");
        }

        if (!ALLOWED_FORMATS.contains(file.getContentType())) {
            throw new IllegalArgumentException("Invalid file type. Allowed: jpg, jpeg, png, gif.");
        }

        if (file.getSize() > 1048576) { // 1MB limit
            throw new IllegalArgumentException("File size exceeds the 1MB limit.");
        }

        Optional<User> userOptional = usersRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = userOptional.get();
        String oldImageUrl = user.getImageUrl();

        try {
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                String publicId = oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1, oldImageUrl.lastIndexOf("."));
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());
            String newImageUrl = (String) uploadResult.get("secure_url");

            usersRepository.updateUserImage(userId, newImageUrl);

            return newImageUrl;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

    @Override
    @Transactional
    public void deleteUserImage(Long userId) {
        Optional<User> userOptional = usersRepository.findById(userId);
        if (userOptional.isEmpty()) {
            throw new IllegalArgumentException("User not found.");
        }

        User user = userOptional.get();
        String oldImageUrl = user.getImageUrl();

        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            try {
                String publicId = oldImageUrl.substring(oldImageUrl.lastIndexOf("/") + 1, oldImageUrl.lastIndexOf("."));
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            } catch (IOException e) {
                throw new RuntimeException("Failed to delete image", e);
            }
        }

        usersRepository.updateUserImage(userId, null);
    }
}
