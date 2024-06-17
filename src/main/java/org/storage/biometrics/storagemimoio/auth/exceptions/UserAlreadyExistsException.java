package org.storage.biometrics.storagemimoio.auth.exceptions;

public class UserAlreadyExistsException extends RuntimeException {
    public UserAlreadyExistsException(String format) {
        super(format);
    }
}
