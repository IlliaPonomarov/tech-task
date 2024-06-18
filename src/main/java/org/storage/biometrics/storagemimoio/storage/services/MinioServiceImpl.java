package org.storage.biometrics.storagemimoio.storage.services;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.Metadata;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateUploadResponse;
import org.storage.biometrics.storagemimoio.storage.exceptions.InitiatingUploadException;
import org.storage.biometrics.storagemimoio.storage.exceptions.MinioBucketNotFoundException;
import org.storage.biometrics.storagemimoio.storage.exceptions.MinioUploadException;
import org.storage.biometrics.storagemimoio.utilit.enums.BucketTypes;

import java.io.InputStream;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

@Service("minioServiceImpl")
public class MinioServiceImpl implements MinioService{

    private final MinioClient minioClient;

    @Value("${minio.expirationTime}")
    private int expirationTime;

    @Autowired
    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @Override
    public InitiateUploadResponse generatePresignedUrl(final String objectName) {
        var bucketName = determineBucketBasedOnFileName(objectName);

        if (!isBucketExists(bucketName)) {
            throw new MinioBucketNotFoundException(
                    String.format("Bucket %s not found", bucketName));
        }

        try {
            var url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expirationTime)
                            .build()
            );

            return new InitiateUploadResponse(url, new Metadata(bucketName, objectName));

        } catch (Exception e) {
            throw new InitiatingUploadException(
                    String.format("Error while generating presigned URL for %s", objectName),
                    e);
        }
    }

    @Override
    public BinaryUploadResponse uploadFile(final String bucketName, final MultipartFile file) {
        var type = file.getContentType();
        var objectName = file.getOriginalFilename();

        try{
            InputStream stream = file.getInputStream();

            if (!isBucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                throw new MinioBucketNotFoundException(
                       String.format("Bucket %s not found", bucketName));

            }

            var response = minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(stream, stream.available(), -1)
                            .contentType(type).build());

            return new BinaryUploadResponse(response.object(), response.versionId());

        } catch (Exception e) {
            throw new MinioUploadException(
                    String.format("Error while uploading file %s to bucket %s", objectName, bucketName));
        }
    }

    private String determineBucketBasedOnFileName(final String fileName) {
        var buckets = List.of(BucketTypes.values());
        Predicate<BucketTypes> fileContainsBucketName = bucket -> fileName.contains(bucket.getBucketName());
        Function<BucketTypes, String> getBucketName = BucketTypes::getBucketName;

        return buckets
                .stream()
                .filter(fileContainsBucketName)
                .map(getBucketName)
                .findFirst()
                .orElseThrow(
                    () -> new MinioBucketNotFoundException(String.format("Bucket for file %s not found", fileName)
                ));
    }

    private boolean isBucketExists(final String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
