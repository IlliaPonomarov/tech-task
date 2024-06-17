package org.storage.biometrics.storagemimoio.utilit;

public enum MimeType {
    OCTET_STREAM("application/octet-stream");

    private final String mimeType;

    MimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public static MimeType fromString(String mimeType) {
        for (MimeType value : MimeType.values()) {
            if (value.mimeType.equals(mimeType)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown MIME type: " + mimeType);
    }
}
