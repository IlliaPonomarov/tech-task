package org.storage.biometrics.storagemimoio.storage.dtos;

import java.util.Date;
import java.util.UUID;

public record Metadata(UUID fileId, long userId, String bucket, String filename, Date expirePreSignedUrl) {
    public Metadata {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
    }


}
