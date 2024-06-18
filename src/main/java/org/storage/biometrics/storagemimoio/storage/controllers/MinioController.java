package org.storage.biometrics.storagemimoio.storage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryDownloadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateDownloadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateUploadResponse;
import org.storage.biometrics.storagemimoio.storage.services.MinioService;
import org.storage.biometrics.storagemimoio.utilit.exceptions.ErrorMessage;
import org.storage.biometrics.storagemimoio.utilit.validators.annotations.FilenameValid;
import org.storage.biometrics.storagemimoio.utilit.validators.annotations.PreSignedURLValid;


import java.util.Optional;

@RestController
@RequestMapping("/api/v3/minio")
@Validated
@Tag(name = "Minio", description = "Minio storage operations")
public class MinioController {
    private final MinioService minioService;

    @Autowired
    public MinioController(@Qualifier("minioServiceImpl") MinioService minioService) {
        this.minioService = minioService;
    }


    @Operation(summary = "Initiate upload, get pre-signed URL to upload file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pre-signed URL generated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = InitiateUploadResponse.class))
            }),

            @ApiResponse(responseCode = "400", description = "Invalid filename", content ={
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error while generating presigned URL", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
            })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/initiate/upload/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public InitiateUploadResponse initiateUpload(@Valid @FilenameValid @PathVariable String fileName) {
        return Optional.ofNullable(minioService.generatePreSignedUploadUrl(fileName))
                .orElseThrow(() -> new RuntimeException("Error while generating presigned URL"));
    }


    @GetMapping("/initiate/download/{fileName}")
    @ResponseStatus(HttpStatus.OK)
    public InitiateDownloadResponse initiateDownload(@Valid @FilenameValid String fileName) {

        return Optional.ofNullable(minioService.generatePreSignedDownloadUrl(fileName))
                .orElseThrow(() -> new RuntimeException("Error while generating presigned URL"));
    }

    @Operation(summary = "Upload file to Minio storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File uploaded successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = BinaryUploadResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid presigned URL", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error while uploading file", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
            })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    @ResponseStatus(HttpStatus.OK)
    public BinaryUploadResponse uploadFile(@PreSignedURLValid @RequestParam("url") String preSignedURL, @RequestParam MultipartFile file) {

        return minioService.uploadFile(preSignedURL, file);
    }

    @Operation(summary = "Download file from Minio storage")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "File downloaded successfully", content = {
                    @Content(mediaType = "application/octet-stream", schema = @Schema(implementation = BinaryUploadResponse.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid presigned URL", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
            }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
            }),
            @ApiResponse(responseCode = "500", description = "Error while downloading file", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
            })
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping(value = "/download/{preSignedURL}", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public BinaryDownloadResponse downloadFile(@PathVariable @PreSignedURLValid String preSignedURL) {
        return minioService.downloadFile(preSignedURL);
    }


}
