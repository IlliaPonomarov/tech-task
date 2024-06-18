package org.storage.biometrics.storagemimoio.storage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateUploadResponse;
import org.storage.biometrics.storagemimoio.storage.services.MinioService;

import java.util.Optional;

@RestController
@RequestMapping("/api/v3/minio")
public class MinioController {
    private final MinioService minioService;

    @Autowired
    public MinioController(@Qualifier("minioServiceImpl") MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/initiate/upload/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public InitiateUploadResponse initiateBucket(@PathVariable String fileName) {


        return Optional.ofNullable(minioService.generatePresignedUrl(fileName))
                .orElseThrow(() -> new RuntimeException("Error while generating presigned URL"));
    }

    @PostMapping("/upload/{presignedURL}")
    @ResponseStatus(HttpStatus.OK)
    public BinaryUploadResponse uploadFile(@PathVariable String presignedURL, @RequestParam MultipartFile file) {

        return minioService.uploadFile(presignedURL, file);
    }
}
