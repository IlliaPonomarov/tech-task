package org.storage.biometrics.storagemimoio.storage.services.impl;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.storage.biometrics.storagemimoio.storage.dtos.*;
import org.storage.biometrics.storagemimoio.storage.exceptions.*;
import org.storage.biometrics.storagemimoio.storage.services.MinioService;
import org.storage.biometrics.storagemimoio.utilit.enums.BucketTypes;
import org.storage.biometrics.storagemimoio.utilit.enums.InitiateTypes;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service("minioServiceImpl")
public class MinioServiceImpl implements MinioService {

    private final MinioClient minioClient;

    @Value("${minio.expirationTime}")
    private int expirationTime;

    @Autowired
    public MinioServiceImpl(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void createTempBucket() {
        try {
            if (!isBucketExists("temp")) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("temp").build());
            }
        } catch (Exception e) {
            throw new MinioBucketWasNotCreatedException("Error while creating temp bucket");
        }
    }

    /**
     * Method to generate pre-signed URL for uploading file to Minio
     * @param objectName is the name of the file
     * @param userId - user id
     * @return InitiateUploadResponse - URL and Metadata
     */

    @Override
    public InitiateUploadResponse generatePreSignedUploadUrl(final String objectName, final String bucketName, long userId) {

        try {
            if (!isExpirationTimeValid(expirationTime)) {
                throw new ExpirationTimeException("Expiration time must be between 1 second and 7 days");
            }

            if (!isBucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                throw new MinioBucketNotFoundException(
                        String.format("Bucket %s not found", bucketName));
            }

            var url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucketName)
                            .object(objectName)
                            .expiry(expirationTime)
                            .build()
            );

            /// expiration date from now + expirationTime
            var expireDate = LocalDateTime.now().plusSeconds(expirationTime);

            // here we should save info about file in database and return
            //  ...

            return new InitiateUploadResponse(url, InitiateTypes.UPLOAD, expireDate, new Metadata(UUID.randomUUID(), bucketName, objectName, new Date(), new Date()));

        } catch (Exception e) {
            throw new InitiatingAdminUploadException(
                    String.format("Error while %s", e.getMessage()), e);
        }
    }


    @Override
    public InitiateDownloadResponse generatePreSignedDownloadUrl(final String fileName, final String bucketName, long userId) {

        try {
            if (!isBucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                throw new MinioBucketNotFoundException(
                        String.format("Bucket %s not found", bucketName));
            }

            if (!isExpirationTimeValid(expirationTime)) {
                throw new ExpirationTimeException("Expiration time must be between 1 second and 7 days");
            }

            var url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(expirationTime)
                            .build()
            );

            var expirationDate = LocalDateTime.now().plusSeconds(expirationTime);

            /*
             * here we should save info about file in database and return Metadata
             * ...
             */

            return new InitiateDownloadResponse(url, InitiateTypes.DOWNLOAD, expirationDate, new Metadata(UUID.randomUUID(), bucketName, fileName, new Date(), new Date()));

        } catch (Exception e) {
            throw new InitiatingAdminUploadException(
                    String.format("Error while %s", e.getMessage()), e);
        }
    }

    public boolean isBucketExists(final String bucketName) {
        try {
       //     var buckets = List.of(BucketTypes.values());

//            for (var bucket1 : buckets) {
//                boolean isBucketExists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
//                if (!isBucketExists) {
//                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket1.getBucketName()).build());
//                }
//            }

            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isExpirationTimeValid(int expirationTime) {
        return expirationTime > 0 && expirationTime < 604800;
    }




}
