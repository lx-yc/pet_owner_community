package com.example.service;

import org.springframework.web.multipart.MultipartFile;

public interface UploadService {
    String uploadImage(MultipartFile file, String type);

    void deleteImage(String imageUrl);
}
