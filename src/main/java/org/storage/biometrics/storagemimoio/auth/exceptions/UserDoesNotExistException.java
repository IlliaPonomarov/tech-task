package org.storage.biometrics.storagemimoio.auth.exceptions;

public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(String format) {
        super(format);
    }
}
