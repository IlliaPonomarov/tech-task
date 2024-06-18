package org.storage.biometrics.storagemimoio.storage.exceptions;

public class ExpirationTimeException extends RuntimeException {
    public ExpirationTimeException(String format) {
        super(format);
    }
}
