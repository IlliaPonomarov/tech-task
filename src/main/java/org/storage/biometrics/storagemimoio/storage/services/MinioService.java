package org.storage.biometrics.storagemimoio.storage.services;

import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.PreSignedURL;

public interface MinioService {

    PreSignedURL generatePresignedUrl(String bucketName, String fileName);

    BinaryUploadResponse uploadFile(String bucketName, MultipartFile file);


}
