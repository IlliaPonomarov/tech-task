package org.storage.biometrics.storagemimoio.utilit.validators.annotations;


import jakarta.validation.Constraint;
import org.storage.biometrics.storagemimoio.utilit.validators.FilenameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = FilenameValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface FilenameValid {
    String message() default "Filename is not valid";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
