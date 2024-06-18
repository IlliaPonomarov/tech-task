package org.storage.biometrics.storagemimoio.storage.dtos;

import java.util.Arrays;

public record BinaryDownloadResponse(byte[] file, boolean metadata) {
    public BinaryDownloadResponse {
        if (file == null || file.length == 0) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }

    }

    @Override
    public String toString() {
        return String.format(
                "BinaryDownloadResponse{file=%s, isSuccessful=%s}",
                Arrays.toString(file),
                metadata
        );
    }
}
