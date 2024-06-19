package org.storage.biometrics.storagemimoio.utilit.enums;

public enum InitiateTypes {
    DOWNLOAD("download"),
    UPLOAD("upload");

    private final String type;

    InitiateTypes(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public static InitiateTypes fromString(String text) {
        for (InitiateTypes b : InitiateTypes.values()) {
            if (b.type.equalsIgnoreCase(text)) {
                return b;
            }
        }
        return null;
    }
}
