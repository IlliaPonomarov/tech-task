package org.storage.biometrics.storagemimoio.storage.dtos;

import org.storage.biometrics.storagemimoio.utilit.MimeType;

import java.util.Date;

public record Metadata(String mimeType, String filename) {
    public Metadata {
        if (filename == null || filename.isBlank()) {
            throw new IllegalArgumentException("Filename cannot be null or empty");
        }
    }

    @Override
    public String toString() {
        return String.format(
                "Metadata{mimeType=%s, filename='%s'}",
                mimeType,
                filename
        );
    }
}
