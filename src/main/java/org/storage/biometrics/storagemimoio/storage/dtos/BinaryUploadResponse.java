package org.storage.biometrics.storagemimoio.storage.dtos;

public record BinaryUploadResponse (String fileName, String bucketName) {
    @Override
    public String toString() {
        return String.format(
                "BinaryUploadResponse{fileName='%s', bucketName='%s'}",
                fileName,
                bucketName
        );
    }
}
