package org.storage.biometrics.storagemimoio.storage.dtos;

import org.storage.biometrics.storagemimoio.utilit.enums.InitiateTypes;
import java.time.LocalDateTime;


public record InitiateUploadResponse(String preSignedUploadURL,
                                     InitiateTypes types,
                                     LocalDateTime expiresAt,
                                     Metadata metadata) {
}
