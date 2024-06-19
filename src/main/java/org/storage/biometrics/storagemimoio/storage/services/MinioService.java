package org.storage.biometrics.storagemimoio.storage.services;

import org.storage.biometrics.storagemimoio.storage.dtos.InitiateDownloadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateUploadResponse;

public interface MinioService {

    InitiateUploadResponse generatePreSignedUploadUrl(final String fileName, final String bucketName, long userId);

    InitiateDownloadResponse generatePreSignedDownloadUrl(final String fileName, final String bucketName, long userId);

    boolean isBucketExists(String bucketName);
}
