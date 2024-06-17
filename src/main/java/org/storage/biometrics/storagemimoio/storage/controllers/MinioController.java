package org.storage.biometrics.storagemimoio.storage.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.PreSignedURL;
import org.storage.biometrics.storagemimoio.storage.services.MinioService;

@RestController
@RequestMapping("/api/v3/minio")
public class MinioController {
    private final MinioService minioService;

    @Autowired
    public MinioController(@Qualifier("minioServiceImpl") MinioService minioService) {
        this.minioService = minioService;
    }

    @GetMapping("/initiate/{bucketName}/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public PreSignedURL initiateBucket(@PathVariable String bucketName, @PathVariable String fileName) {
        var preSignedURL = minioService.generatePresignedUrl(bucketName, fileName);

        return new PreSignedURL("Bucket initiated", null);
    }

    @PostMapping("/upload/{bucketName}")
    @ResponseStatus(HttpStatus.OK)
    public BinaryUploadResponse uploadFile(@PathVariable String bucketName, @RequestParam MultipartFile file) {

        return minioService.uploadFile(bucketName, file);
    }
}
