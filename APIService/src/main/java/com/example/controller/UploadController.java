package com.example.controller;

import com.example.service.UploadService;
import com.example.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "文件上传", description = "图片等文件上传到阿里云OSS")
@RestController
@RequestMapping("/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    @Operation(summary = "上传图片", description = "上传图片到阿里云OSS，type参数：avatar-用户头像，pet-宠物图片，post-帖子图片")
    @PostMapping("/image")
    public Result<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "type", defaultValue = "pet") String type) {
        try {
            String fileUrl = uploadService.uploadImage(file, type);
            return Result.success(fileUrl, "上传成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    @Operation(summary = "删除图片", description = "从阿里云OSS删除图片，需要提供完整的图片URL")
    @PostMapping("/deleteImage")
    public Result<Void> deleteImage(@RequestParam("imageUrl") String imageUrl) {
        try {
            uploadService.deleteImage(imageUrl);
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
