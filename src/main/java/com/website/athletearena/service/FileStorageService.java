package com.website.athletearena.service;

import com.website.athletearena.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    @Value("${file.upload-dir}")
    private String uploadDir;
    
    public String storeFile(MultipartFile file, String subfolder) {
        try {
            if (file.isEmpty()) {
                throw new BadRequestException("Failed to store empty file");
            }
            
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(uploadDir, subfolder);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                    originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
            String filename = UUID.randomUUID().toString() + extension;
            
            // Copy file to upload directory
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return "/uploads/" + subfolder + "/" + filename;
            
        } catch (IOException ex) {
            throw new BadRequestException("Failed to store file: " + ex.getMessage());
        }
    }
    
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null && !fileUrl.isEmpty()) {
                Path filePath = Paths.get(uploadDir + fileUrl.replace("/uploads", ""));
                Files.deleteIfExists(filePath);
            }
        } catch (IOException ex) {
            throw new BadRequestException("Failed to delete file: " + ex.getMessage());
        }
    }
}