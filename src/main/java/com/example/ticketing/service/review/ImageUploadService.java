package com.example.ticketing.service.review;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageUploadService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${server.address:localhost}")
    private String serverAddress;

    @Value("${server.port}")
    private int serverPort;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            log.error("Could not create upload directory: {}", uploadDir, e);
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String uploadImage(MultipartFile image) throws IOException {
        if (image == null || image.isEmpty()) {
            throw new IllegalArgumentException("Image file cannot be empty");
        }

        // 파일 확장자 검증
        String originalFilename = image.getOriginalFilename();
        String extension = getExtension(originalFilename);
        validateImageExtension(extension);

        // 파일명 생성 및 저장
        String fileName = generateFileName(originalFilename);
        Path filePath = Paths.get(uploadDir, fileName);

        try {
            Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return generateImageUrl(fileName);
        } catch (IOException e) {
            log.error("Failed to save image: {}", fileName, e);
            throw new IOException("Failed to save image: " + fileName, e);
        }
    }

    private String generateFileName(String originalFilename) {
        return UUID.randomUUID().toString() + "_" + originalFilename;
    }

    private String generateImageUrl(String fileName) {
        return String.format("http://%s:%d/images/%s", serverAddress, serverPort, fileName);
    }

    private String getExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");
    }

    private void validateImageExtension(String extension) {
        Set<String> validExtensions = Set.of("jpg", "jpeg", "png", "gif");
        if (!validExtensions.contains(extension.toLowerCase())) {
            throw new IllegalArgumentException("Invalid image format. Supported formats are: " + validExtensions);
        }
    }
}
