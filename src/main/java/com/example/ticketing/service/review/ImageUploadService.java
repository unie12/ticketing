package com.example.ticketing.service.review;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
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

        String extension = getExtension(image.getOriginalFilename());
        validateImageExtension(extension);
        String fileName = generateFileName(image.getOriginalFilename());

        BufferedImage originalImage = ImageIO.read(image.getInputStream());
        if (originalImage == null) {
            throw new IOException("Failed to read image file");
        }

        // 최종 리사이징 (400px)
        int targetWidth = Math.min(originalImage.getWidth(), 400);
        BufferedImage resizedImage = resizeImage(originalImage, targetWidth);

        Path filePath = Paths.get(uploadDir).resolve(fileName);
        saveCompressedImage(resizedImage, filePath.toString());

        return generateImageUrl(fileName);
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth) {
        // 항상 리사이징을 강제로 수행
        double ratio = (double) targetWidth / originalImage.getWidth();
        int targetHeight = (int) (originalImage.getHeight() * ratio);

        // 이미지 타입을 RGB로 강제 변환
        BufferedImage resizedImage = new BufferedImage(
                targetWidth,
                targetHeight,
                BufferedImage.TYPE_INT_RGB  // ARGB 대신 RGB 사용
        );

        // 리사이징 품질 향상
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();

        return resizedImage;
    }


    private void saveCompressedImage(BufferedImage image, String path) throws IOException {
        File output = new File(path);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("No image writer found");
        }

        ImageWriter writer = writers.next();
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
            writer.setOutput(ios);
            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.6f); // 60% 품질
            }
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
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
