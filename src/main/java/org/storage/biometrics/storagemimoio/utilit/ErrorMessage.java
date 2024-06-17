package org.storage.biometrics.storagemimoio.utilit;

import java.util.Date;

public record ErrorMessage (String message, Date date) {
    @Override
    public String toString() {
        return "ErrorMessage{message='%s', date=%s, fullMessage='%s'}".formatted(message, date);
    }
}
