package org.storage.biometrics.storagemimoio.storage.dtos;

public record PreSignedURL(String url, Metadata metadata) {

    @Override
    public String toString() {
        return String.format(
                "PreSignedURLResponse{url='%s', metadata=%s}",
                url,
                metadata
        );
    }
}
