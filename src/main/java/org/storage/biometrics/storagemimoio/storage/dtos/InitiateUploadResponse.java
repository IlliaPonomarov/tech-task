package org.storage.biometrics.storagemimoio.storage.dtos;

import org.storage.biometrics.storagemimoio.utilit.enums.InitiateTypes;

import java.util.Date;

public record InitiateUploadResponse(String preSignedUploadURL,
                                     InitiateTypes types,
                                     Date expiresAt,
                                     Metadata metadata) {
}
