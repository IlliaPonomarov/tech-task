package org.storage.biometrics.storagemimoio.storage.exceptions;

public class InitiatingAdminUploadException extends RuntimeException {
    public InitiatingAdminUploadException(String format, Exception e) {
        super(format, e);
    }
}
