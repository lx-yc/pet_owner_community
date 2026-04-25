package com.example.controller;

import com.example.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Tag(name = "文件上传", description = "图片等文件上传接口")
@RestController
@RequestMapping("/upload")
public class UploadController {

    private static final String UPLOAD_DIR = System.getProperty("user.home") + "/Desktop/pet-community-uploads/";

    @Operation(summary = "上传图片", description = "上传图片到本地桌面目录")
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("上传文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String newFilename = UUID.randomUUID().toString().replace("-", "") + extension;

        File destDir = new File(UPLOAD_DIR);
        if (!destDir.exists()) {
            destDir.mkdirs();
        }

        File destFile = new File(destDir, newFilename);
        try {
            file.transferTo(destFile);
            String fileUrl = "http://localhost:8080/uploads/" + newFilename;
            return Result.success(fileUrl, "上传成功");
        } catch (IOException e) {
            return Result.error("上传失败: " + e.getMessage());
        }
    }
}
