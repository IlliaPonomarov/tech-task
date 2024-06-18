package org.storage.biometrics.storagemimoio.storage.exceptions;

public class MinioUploadException extends RuntimeException {
    public MinioUploadException(String format) {
        super(format);
    }
}
