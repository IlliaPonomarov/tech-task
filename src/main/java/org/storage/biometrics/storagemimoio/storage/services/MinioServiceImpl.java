package org.storage.biometrics.storagemimoio.storage.services;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.Metadata;
import org.storage.biometrics.storagemimoio.storage.dtos.PreSignedURL;
import org.storage.biometrics.storagemimoio.storage.exceptions.MinioBucketNotFoundException;

import java.io.InputStream;
import java.util.Collections;
import java.util.Set;

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
    public PreSignedURL generatePresignedUrl(String bucketName, String fileName) {
        if (!isBucketExists(bucketName)) {
            throw new MinioBucketNotFoundException(
                    String.format("Bucket %s not found", bucketName));
        }

        try {
            var url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucketName)
                            .object(fileName)
                            .expiry(expirationTime)
                            .build());

            return new PreSignedURL(url, new Metadata(bucketName, fileName));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public BinaryUploadResponse uploadFile(String bucketName, MultipartFile file) {
        var type = file.getContentType();
        var objectName = file.getOriginalFilename();

        try{
            InputStream stream = file.getInputStream();

            if (!isBucketExists(bucketName)) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                throw new MinioBucketNotFoundException(
                       String.format("Bucket %s not found", bucketName));

            }

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectName)
                            .stream(stream, stream.available(), -1)
                            .contentType(type).build());

            return new BinaryUploadResponse(objectName, bucketName);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isBucketExists(String bucketName) {
        try {
            return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
