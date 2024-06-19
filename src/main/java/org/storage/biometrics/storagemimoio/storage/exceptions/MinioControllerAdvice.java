package org.storage.biometrics.storagemimoio.storage.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.storage.biometrics.storagemimoio.utilit.exceptions.ErrorMessage;

import java.util.Date;

@RestControllerAdvice
public class MinioControllerAdvice {

    @ExceptionHandler(MinioBucketNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleBucketNotFoundException(MinioBucketNotFoundException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(InitiatingAdminUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleInitiatingUploadException(InitiatingAdminUploadException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(MinioUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleMinioUploadException(MinioUploadException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(ExpirationTimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleExpirationTimeException(ExpirationTimeException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(MinioDownloadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleMinioDownloadException(MinioDownloadException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(MinioBucketWasNotCreatedException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleMinioBucketWasNotCreatedException(MinioBucketWasNotCreatedException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

}
