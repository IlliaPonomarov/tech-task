package org.storage.biometrics.storagemimoio.storage.services;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
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
import org.storage.biometrics.storagemimoio.storage.dtos.*;
import org.storage.biometrics.storagemimoio.storage.exceptions.*;
import org.storage.biometrics.storagemimoio.utilit.enums.BucketTypes;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;
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

    @Override
    public InitiateUploadResponse generatePreSignedUploadUrl(final String objectName) {
        var bucketName = determineBucketBasedOnFileName(objectName);

        // expiry must be minimum 1 second to maximum 7 days
        if (expirationTime < 1 || expirationTime > 604800) {
            throw new ExpirationTimeException("Expiration time must be between 1 second and 7 days");
        }

        try {
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

            return new InitiateUploadResponse(url, new Metadata(bucketName, objectName));

        } catch (Exception e) {
            throw new InitiatingUploadException(
                    String.format("Error while %s", e.getMessage()), e);
        }
    }

    @Override
    public BinaryUploadResponse uploadFile(final String preSignedUrl, final MultipartFile file) {
        var objectName = file.getOriginalFilename();
        try {
            var client = new OkHttpClient().newBuilder().build();
            var mediaType = okhttp3.MediaType.parse("application/octet-stream");
            var requestBody = okhttp3.RequestBody.create(mediaType, file.getBytes());
            var request = new okhttp3.Request.Builder()
                    .url(preSignedUrl)
                    .method("PUT", requestBody)
                    .build();

            var response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new MinioUploadException("Error while uploading file");
            }

            return new BinaryUploadResponse(objectName, determineBucketBasedOnFileName(objectName));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public InitiateDownloadResponse generatePreSignedDownloadUrl(String fileName) {
        var bucketName = determineBucketBasedOnFileName(fileName);

        try {
            if (!isBucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                throw new MinioBucketNotFoundException(
                        String.format("Bucket %s not found", bucketName));
            }

            var url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(expirationTime)
                            .build()
            );

            return new InitiateDownloadResponse(url, new Metadata(bucketName, fileName));

        } catch (Exception e) {
            throw new InitiatingUploadException(
                    String.format("Error while %s", e.getMessage()), e);
        }
    }

    @Override
    public BinaryDownloadResponse downloadFile(String preSignedURL) {
        var client = new OkHttpClient().newBuilder().build();
        var request = new okhttp3.Request.Builder()
                .url(preSignedURL)
                .method("GET", null)
                .build();

        try {
            var response = client.newCall(request).execute();

            if (!response.isSuccessful()) {
                throw new MinioDownloadException("Error while downloading file");
            }

            Optional<InputStream> inputStream = Optional.ofNullable(response.body()).map(okhttp3.ResponseBody::byteStream);

            if (inputStream.isEmpty() || inputStream.orElseThrow().readAllBytes().length == 0) {
                throw new MinioDownloadException("Error while downloading file");
            }

            return new BinaryDownloadResponse(inputStream.orElseThrow().readAllBytes(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
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
