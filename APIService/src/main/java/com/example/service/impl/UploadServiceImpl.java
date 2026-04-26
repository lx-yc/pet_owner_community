package com.example.service.impl;

import com.aliyun.oss.OSS;
import com.example.config.OssConfig;
import com.example.service.UploadService;
import jakarta.annotation.Resource;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

@Service
public class UploadServiceImpl implements UploadService {

    @Resource
    private OSS ossClient;

    @Resource
    private OssConfig ossConfig;

    @Override
    public String uploadImage(MultipartFile file, String type) {
        if (file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        String folderName = getFolderNameByType(type);
        String objectName = generateObjectName(folderName, file.getOriginalFilename());

        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(ossConfig.getBucketName(), objectName, inputStream);
            return buildFileUrl(objectName);
        } catch (IOException e) {
            throw new RuntimeException("上传失败: " + e.getMessage());
        }
    }

    @Override
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            throw new RuntimeException("图片URL不能为空");
        }

        String bucketDomain = "https://" + ossConfig.getBucketName() + "." +
                            ossConfig.getEndpoint().replace("https://", "") + "/";

        if (!imageUrl.startsWith(bucketDomain)) {
            throw new RuntimeException("无效的图片URL");
        }

        String objectName = imageUrl.substring(bucketDomain.length());
        ossClient.deleteObject(ossConfig.getBucketName(), objectName);
    }

    private String getFolderNameByType(String type) {
        return switch (type.toLowerCase()) {
            case "avatar" -> "user_Images/";
            case "post" -> "post_Images/";
            case "pet" -> "pet_Images/";
            default -> "pet_Images/";
        };
    }

    private String generateObjectName(String folderName, String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;
        return folderName + newFilename;
    }

    private String buildFileUrl(String objectName) {
        return "https://" + ossConfig.getBucketName() + "." +
               ossConfig.getEndpoint().replace("https://", "") + "/" + objectName;
    }
}
