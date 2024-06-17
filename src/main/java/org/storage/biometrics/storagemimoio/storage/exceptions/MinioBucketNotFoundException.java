package org.storage.biometrics.storagemimoio.storage.exceptions;

public class MinioBucketNotFoundException extends RuntimeException {
    public MinioBucketNotFoundException(String format) {
        super(format);
    }
}
