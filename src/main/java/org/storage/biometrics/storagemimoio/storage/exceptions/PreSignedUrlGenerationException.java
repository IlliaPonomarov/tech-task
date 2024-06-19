package org.storage.biometrics.storagemimoio.storage.exceptions;

public class PreSignedUrlGenerationException extends RuntimeException {
    public PreSignedUrlGenerationException(String errorWhileGeneratingPresignedUrl) {
        super(errorWhileGeneratingPresignedUrl);
    }
}
