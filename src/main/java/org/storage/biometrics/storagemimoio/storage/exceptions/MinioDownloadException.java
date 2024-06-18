package org.storage.biometrics.storagemimoio.storage.exceptions;

public class MinioDownloadException extends RuntimeException {
    public MinioDownloadException(String errorWhileDownloadingFile) {
        super(errorWhileDownloadingFile);
    }
}
