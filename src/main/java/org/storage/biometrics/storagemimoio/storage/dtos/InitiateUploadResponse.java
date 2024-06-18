package org.storage.biometrics.storagemimoio.storage.dtos;

public record InitiateUploadResponse(String preSignedUploadURL, Metadata metadata) {

    @Override
    public String toString() {
        return String.format(
                "PreSignedURLResponse{preSignedUploadURL='%s', metadata=%s}",
                preSignedUploadURL,
                metadata
        );
    }
}
