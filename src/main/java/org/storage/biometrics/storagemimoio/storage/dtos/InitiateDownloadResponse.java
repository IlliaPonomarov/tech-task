package org.storage.biometrics.storagemimoio.storage.dtos;

import org.storage.biometrics.storagemimoio.utilit.enums.InitiateTypes;

import java.time.LocalDateTime;
import java.util.Date;

public record InitiateDownloadResponse(String preSignedUploadURL,
                                       InitiateTypes types,
                                       LocalDateTime expiresAt,
                                       Metadata metadata) {
}
