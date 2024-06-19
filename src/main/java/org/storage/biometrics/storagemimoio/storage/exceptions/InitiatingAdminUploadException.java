package org.storage.biometrics.storagemimoio.storage.exceptions;

public class InitiatingUploadException extends RuntimeException {
    public InitiatingUploadException(String format, Exception e) {
        super(format, e);
    }
}
