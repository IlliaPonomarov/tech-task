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

    @ExceptionHandler(InitiatingUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleInitiatingUploadException(InitiatingUploadException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

    @ExceptionHandler(MinioUploadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorMessage handleMinioUploadException(MinioUploadException e) {
        return new ErrorMessage(e.getMessage(), new Date());
    }

//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
//    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
//        return new ErrorMessage(e.getMessage(), new Date());
//    }
}
