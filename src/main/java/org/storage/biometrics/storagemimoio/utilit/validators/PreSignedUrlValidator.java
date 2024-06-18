package org.storage.biometrics.storagemimoio.utilit.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.storage.biometrics.storagemimoio.utilit.validators.annotations.PreSignedURLValid;

public class PreSignedUrlValidator implements ConstraintValidator<PreSignedURLValid, String> {
    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.startsWith("http://");
    }
}
