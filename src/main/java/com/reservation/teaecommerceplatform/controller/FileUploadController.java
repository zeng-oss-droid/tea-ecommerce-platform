package com.reservation.teaecommerceplatform.controller;

import com.reservation.teaecommerceplatform.common.Result;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/** 本地磁盘保存图片，返回可给前端的 URL；路径受 AdminInterceptor 保护，需管理员 Token（商家上传需同 Token 策略视部署而定）。 */
@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    @Value("${file.upload.path:uploads/}")
    private String uploadPath;

    @Value("${file.upload.url:http://localhost:8080/uploads/}")
    private String uploadUrl;

    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return Result.error("文件不能为空");
            }

            // 检查文件类型
            String originalFilename = file.getOriginalFilename();
            String extension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            
            if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
                return Result.error("只支持jpg、jpeg、png、gif、webp格式的图片");
            }

            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + extension;
            Path filePath = Paths.get(uploadPath + fileName);

            // 保存文件
            Files.write(filePath, file.getBytes());

            // 返回文件URL
            String fileUrl = uploadUrl + fileName;
            return Result.success("上传成功", fileUrl);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }

    @PostMapping("/images")
    public Result<List<String>> uploadImages(@RequestParam("files") MultipartFile[] files) {
        try {
            List<String> urls = new ArrayList<>();
            
            for (MultipartFile file : files) {
                if (file.isEmpty()) {
                    continue;
                }

                // 检查文件类型
                String originalFilename = file.getOriginalFilename();
                String extension = "";
                if (originalFilename != null && originalFilename.contains(".")) {
                    extension = originalFilename.substring(originalFilename.lastIndexOf("."));
                }
                
                if (!extension.matches("\\.(jpg|jpeg|png|gif|webp)$")) {
                    continue;
                }

                // 创建上传目录
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // 生成唯一文件名
                String fileName = UUID.randomUUID().toString() + extension;
                Path filePath = Paths.get(uploadPath + fileName);

                // 保存文件
                Files.write(filePath, file.getBytes());

                // 添加文件URL
                String fileUrl = uploadUrl + fileName;
                urls.add(fileUrl);
            }

            return Result.success("上传成功", urls);
        } catch (IOException e) {
            e.printStackTrace();
            return Result.error("文件上传失败：" + e.getMessage());
        }
    }
}

