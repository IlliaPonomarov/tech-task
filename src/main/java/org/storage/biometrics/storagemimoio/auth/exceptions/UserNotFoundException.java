package org.storage.biometrics.storagemimoio.auth.exceptions;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(String format) {
        super(format);
    }
}
