package org.storage.biometrics.storagemimoio.utilit.enums;

public enum BucketTypes {
    VIDEOS("videos"),
    FACES("faces"),
    FINGERPRINTS("fingerprints"),
    ATTACHMENTS("attachments");

    private final String bucketName;

    BucketTypes(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public static BucketTypes getBucketType(String bucketName) {
        for (BucketTypes bucketType : BucketTypes.values()) {
            if (bucketType.getBucketName().equals(bucketName)) {
                return bucketType;
            }
        }
        return null;
    }
}
