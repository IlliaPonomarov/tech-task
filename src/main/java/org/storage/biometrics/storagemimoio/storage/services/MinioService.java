package org.storage.biometrics.storagemimoio.storage.services;

import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryDownloadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateDownloadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateUploadResponse;

public interface MinioService {

    InitiateUploadResponse generatePreSignedUploadUrl(String fileName);

    BinaryUploadResponse uploadFile(String preSignedUrl, MultipartFile file);


    InitiateDownloadResponse generatePreSignedDownloadUrl(String fileName);

    BinaryDownloadResponse downloadFile(String preSignedURL);
}
