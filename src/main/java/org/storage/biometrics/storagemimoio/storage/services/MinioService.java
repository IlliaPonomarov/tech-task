package org.storage.biometrics.storagemimoio.storage.services;

import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateUploadResponse;

public interface MinioService {

    InitiateUploadResponse generatePresignedUrl(String fileName);

    BinaryUploadResponse uploadFile(String bucketName, MultipartFile file);


}
