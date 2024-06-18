package org.storage.biometrics.storagemimoio.utilit.validators;

import org.springframework.validation.BindingResult;

import java.util.stream.Collectors;

public interface BindingResultUtility {

    static String getErrorMessage(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));
    }
}
