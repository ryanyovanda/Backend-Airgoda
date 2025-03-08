package com.ryanyovanda.airgodabackend.usecase.cloudinary;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface CloudinaryUsecase {
    List<String> uploadImages(List<MultipartFile> images);
}
