package org.storage.biometrics.storagemimoio.utilit.validators.annotations;

import jakarta.validation.Constraint;
import org.storage.biometrics.storagemimoio.utilit.validators.PreSignedUrlValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = PreSignedUrlValidator.class)
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreSignedURLValid {

    String message() default "PreSignedURL is not valid";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};
}
