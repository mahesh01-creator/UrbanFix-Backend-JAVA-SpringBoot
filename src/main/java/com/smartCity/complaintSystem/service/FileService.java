package com.smartCity.complaintSystem.service;

import java.io.File;

import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/";

    public String uploadFile(MultipartFile file) {

        try {
            if (file == null || file.isEmpty()) {
                return null;
            }

            // 🔥 Create folder if not exists
            File directory = new File(uploadDir);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // 🔥 Unique filename
            String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();

            // 🔥 Full path
            File destination = new File(uploadDir + fileName);

            file.transferTo(destination);

            return fileName;

        } catch (Exception e) {
            e.printStackTrace(); // 🔥 VERY IMPORTANT
            throw new RuntimeException("File upload failed: " + e.getMessage());
        }
    }
}