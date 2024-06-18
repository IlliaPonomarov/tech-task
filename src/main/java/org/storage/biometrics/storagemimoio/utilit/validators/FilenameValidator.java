package org.storage.biometrics.storagemimoio.utilit.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.storage.biometrics.storagemimoio.utilit.validators.annotations.FilenameValid;

import java.lang.annotation.Annotation;

public class FilenameValidator implements ConstraintValidator<FilenameValid, String> {


    @Override
    public void initialize(FilenameValid constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String fileName, ConstraintValidatorContext constraintValidatorContext) {
        return fileName != null && !fileName.isEmpty() && !fileName.isBlank() && (fileName.length() >= 3 || fileName.length() <= 255);
    }
}
