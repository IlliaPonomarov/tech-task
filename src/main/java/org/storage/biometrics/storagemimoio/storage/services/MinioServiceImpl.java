package org.storage.biometrics.storagemimoio.storage.services;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.auth.entities.User;
import org.storage.biometrics.storagemimoio.storage.dtos.*;
import org.storage.biometrics.storagemimoio.storage.exceptions.*;
import org.storage.biometrics.storagemimoio.utilit.enums.BucketTypes;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@Service("minioServiceImpl")
public class MinioServiceImpl implements MinioService{

    private final MinioClient minioClient;
    private final RestTemplate restTemplate;

    @Value("${minio.expirationTime}")
    private int expirationTime;

    @Autowired
    public MinioServiceImpl(MinioClient minioClient, RestTemplate restTemplate) {
        this.minioClient = minioClient;
        this.restTemplate = restTemplate;
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
     * @param userId -
     * @return
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

            var expirationDate = new Date(expirationTime);

            // here we should save info about file in database and return
            //  ...

            return new InitiateUploadResponse(url, new Metadata(UUID.randomUUID(), userId, bucketName, objectName, expirationDate));

        } catch (Exception e) {
            throw new InitiatingUploadException(
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

            var expirationDate = new Date(expirationTime);

            /*
             * here we should save info about file in database and return Metadata
             * ...
             */

            return new InitiateDownloadResponse(url, new Metadata(UUID.randomUUID(), userId, bucketName, fileName, expirationDate));

        } catch (Exception e) {
            throw new InitiatingUploadException(
                    String.format("Error while %s", e.getMessage()), e);
        }
    }

    public boolean isBucketExists(final String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isExpirationTimeValid(int expirationTime) {
        return expirationTime > 0 && expirationTime < 604800;
    }




}
