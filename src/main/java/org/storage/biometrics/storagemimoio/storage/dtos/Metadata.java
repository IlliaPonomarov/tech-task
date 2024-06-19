package org.storage.biometrics.storagemimoio.storage.dtos;

import java.util.Date;
import java.util.UUID;

/*
"preSignedUrl": "https://minio.example.com/upload/1234567890",
  "urlType": "upload",
  "expiresAt": "2022-12-31T23:59:59Z",
  "metadata": {
    "fileId": "1234567890",
    "bucketName": "fingerprints",
    "fileName": "fingerprint_1234567890",
    "fileSize": 1024,
    "createdAt": "2022-12-31T23:59:59Z",
    "updatedAt": "2022-12-31T23:59:59Z"
  }
 */

public record Metadata(UUID fileId, String bucketName, String fileName,  Date createdAt, Date updatedAt) {
    public Metadata {
        if (fileId == null) {
            throw new IllegalArgumentException("FileId cannot be null");
        }
        if (bucketName == null || bucketName.isBlank()) {
            throw new IllegalArgumentException("BucketName cannot be null or empty");
        }
        if (fileName == null || fileName.isBlank()) {
            throw new IllegalArgumentException("FileName cannot be null or empty");
        }

        if (createdAt == null) {
            throw new IllegalArgumentException("CreatedAt cannot be null");
        }
        if (updatedAt == null) {
            throw new IllegalArgumentException("UpdatedAt cannot be null");
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Metadata{fileId=%s, bucketName='%s', fileName='%s',  createdAt=%s, updatedAt=%s}",
                fileId,
                bucketName,
                fileName,
                createdAt,
                updatedAt
        );
    }
}
