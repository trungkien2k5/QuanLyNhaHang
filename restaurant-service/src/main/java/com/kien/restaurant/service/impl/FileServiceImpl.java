package com.kien.restaurant.service.impl;

import com.kien.restaurant.exception.UploadFileException;
import com.kien.restaurant.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
public class FileServiceImpl implements FileService {

    private static final Set<String> ALLOWED_EXTENSIONS =
            Set.of(".jpg", ".jpeg", ".png");

    private static final Set<String> ALLOWED_CONTENT_TYPES =
            Set.of("image/jpeg", "image/png");

    @Value("${upload.path}")
    private String uploadPath;

    @Override
    public String upload(MultipartFile file) {

        validateFile(file);

        try {
            Path uploadDir = Paths.get(uploadPath);
            Files.createDirectories(uploadDir);

            String extension = getExtension(file.getOriginalFilename());
            String fileName = UUID.randomUUID() + extension;

            Files.copy(
                    file.getInputStream(),
                    uploadDir.resolve(fileName),
                    StandardCopyOption.REPLACE_EXISTING
            );

            return fileName;

        } catch (IOException e) {
            log.error("Upload file thất bại: originalName={}, uploadPath={}",
                    file.getOriginalFilename(),
                    uploadPath,
                    e);
            throw new UploadFileException("Upload file thất bại");
        }
    }

    @Override
    public void delete(String fileName) {

        if (fileName == null || fileName.isBlank()) {
            return;
        }

        try {
            Files.deleteIfExists(
                    Paths.get(uploadPath).resolve(fileName)
            );

        } catch (IOException e) {
            log.error("Xóa file thất bại: fileName={}, uploadPath={}",
                    fileName,
                    uploadPath,
                    e);
            throw new UploadFileException("Xóa file thất bại");
        }
    }

    private void validateFile(MultipartFile file) {

        if (file == null || file.isEmpty()) {
            throw new UploadFileException("File không được để trống");
        }

        String originalName = file.getOriginalFilename();

        if (originalName == null || originalName.isBlank()) {
            throw new UploadFileException("Tên file không hợp lệ");
        }

        String extension = getExtension(originalName);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new UploadFileException(
                    "Chỉ cho phép upload JPG, JPEG hoặc PNG"
            );
        }

        String contentType = file.getContentType();

        if (contentType == null ||
                !ALLOWED_CONTENT_TYPES.contains(contentType)) {

            throw new UploadFileException(
                    "Định dạng MIME không hợp lệ"
            );
        }
    }

    private String getExtension(String fileName) {

        int index = fileName.lastIndexOf(".");

        if (index == -1) {
            throw new UploadFileException("File không có phần mở rộng");
        }

        return fileName.substring(index).toLowerCase();
    }
}