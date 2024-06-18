package org.storage.biometrics.storagemimoio.storage.dtos;

import org.storage.biometrics.storagemimoio.utilit.validators.annotations.FilenameValid;

public record InitiateUploadRequest(@FilenameValid String filename) {
}
