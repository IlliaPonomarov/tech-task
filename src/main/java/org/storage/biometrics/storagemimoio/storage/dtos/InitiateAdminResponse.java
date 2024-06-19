package org.storage.biometrics.storagemimoio.storage.dtos;

public record InitiateAdminResponse(String preSignedURl, Metadata metadata) {
    public InitiateAdminResponse {
        if (preSignedURl == null || preSignedURl.isBlank()) {
            throw new IllegalArgumentException("PreSigned URL cannot be null or empty");
        }
        if (metadata == null) {
            throw new IllegalArgumentException("Metadata cannot be null");
        }
    }

    @Override
    public String toString() {
        return String.format(
                "InitiateDownloadResponse{preSignedURl='%s', metadata=%s}",
                preSignedURl,
                metadata
        );
    }
}
