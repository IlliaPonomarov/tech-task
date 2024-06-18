package org.storage.biometrics.storagemimoio.storage.dtos;

public record BinaryDownloadResponse(byte[] file, Metadata metadata) {
    public BinaryDownloadResponse {
        if (file == null || file.length == 0) {
            throw new IllegalArgumentException("File cannot be null or empty");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("Metadata cannot be null");
        }
    }

    @Override
    public String toString() {
        return String.format(
                "BinaryDownloadResponse{file=%s, metadata=%s}",
                file,
                metadata
        );
    }
}
