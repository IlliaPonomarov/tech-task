package org.storage.biometrics.storagemimoio.storage.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.storage.biometrics.storagemimoio.storage.dtos.BinaryUploadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateUploadResponse;
import org.storage.biometrics.storagemimoio.storage.services.MinioService;
import org.storage.biometrics.storagemimoio.utilit.exceptions.ErrorMessage;
import org.storage.biometrics.storagemimoio.utilit.validators.annotations.FilenameValid;


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


    @Operation(summary = "Initiate upload, get presigned URL to upload file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Presigned URL generated successfully", content = {
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
    public InitiateUploadResponse initiateUpload(@Valid @FilenameValid String fileName) {

        System.out.println();
        return Optional.ofNullable(minioService.generatePresignedUrl(fileName))
                .orElseThrow(() -> new RuntimeException("Error while generating presigned URL"));
    }

    @PostMapping("/upload/{presignedURL}")
    @ResponseStatus(HttpStatus.OK)
    public BinaryUploadResponse uploadFile(@PathVariable String presignedURL, @RequestParam MultipartFile file) {

        return minioService.uploadFile(presignedURL, file);
    }
}
