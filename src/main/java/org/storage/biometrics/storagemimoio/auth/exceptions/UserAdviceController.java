package org.storage.biometrics.storagemimoio.auth.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.storage.biometrics.storagemimoio.utilit.exceptions.ErrorMessage;

import java.util.Date;

@RestControllerAdvice
public class UserAdviceController {

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ErrorMessage userAlreadyExists(UserAlreadyExistsException ex) {
        return new ErrorMessage(ex.getMessage(), new Date());
    }

    @ExceptionHandler(UserDoesNotExistException.class)
    public ErrorMessage userDoesNotExist(UserDoesNotExistException ex) {
        return new ErrorMessage(ex.getMessage(), new Date());
    }
}
