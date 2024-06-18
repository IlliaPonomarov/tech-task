package org.storage.biometrics.storagemimoio.storage.exceptions;

import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.storage.biometrics.storagemimoio.utilit.ErrorMessage;

import java.util.Date;

@RestControllerAdvice
public class MinioControllerAdvice {

    @ExceptionHandler(MinioBucketNotFoundException.class)
    public ErrorMessage handleBucketNotFoundException(MinioBucketNotFoundException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(InitiatingUploadException.class)
    public ErrorMessage handleInitiatingUploadException(InitiatingUploadException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(MinioUploadException.class)
    public ErrorMessage handleMinioUploadException(MinioUploadException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }
}
