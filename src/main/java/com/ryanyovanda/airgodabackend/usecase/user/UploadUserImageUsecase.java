package com.ryanyovanda.airgodabackend.usecase.user;

import org.springframework.web.multipart.MultipartFile;

public interface UploadUserImageUsecase {
    String uploadUserImage(Long userId, MultipartFile file);
    void deleteUserImage(Long userId);
}
