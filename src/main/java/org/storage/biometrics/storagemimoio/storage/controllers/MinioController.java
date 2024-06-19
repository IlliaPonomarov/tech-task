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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;
import org.storage.biometrics.storagemimoio.auth.exceptions.UserNotFoundException;
import org.storage.biometrics.storagemimoio.auth.services.UserService;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateDownloadResponse;
import org.storage.biometrics.storagemimoio.storage.dtos.InitiateUploadResponse;
import org.storage.biometrics.storagemimoio.storage.exceptions.MinioBucketNotFoundException;
import org.storage.biometrics.storagemimoio.storage.exceptions.PreSignedUrlGenerationException;
import org.storage.biometrics.storagemimoio.storage.services.MinioService;
import org.storage.biometrics.storagemimoio.utilit.exceptions.ErrorMessage;
import org.storage.biometrics.storagemimoio.utilit.validators.annotations.FilenameValid;
import java.util.Optional;
import org.storage.biometrics.storagemimoio.auth.entities.User;

@RestController
@RequestMapping("/api/v3/minio/initiate")
@Validated
@Tag(name = "Minio Storage", description = "Minio storage operations, generate pre-signed URLs for uploading and downloading files")
public class MinioController {
    private final MinioService minioService;
    private final UserService userService;

    @Autowired
    public MinioController(@Qualifier("minioServiceImpl") MinioService minioService, UserService userService) {
        this.minioService = minioService;
        this.userService = userService;
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
    @SecurityRequirement(name = "BearerAuthentication")
    @GetMapping("/upload/{fileName}/{attachmentType}")
    @ResponseStatus(HttpStatus.OK)
    public InitiateUploadResponse initiateUpload(@Valid @FilenameValid @PathVariable String fileName,
                                                 @PathVariable String attachmentType,
                                                 @AuthenticationPrincipal User user) {
        var userOptional = userService.getUserByUsername(user.getUsername());

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(
                    String.format("User with username %s not found", user.getUsername()));
        }

        if (!minioService.isBucketExists(attachmentType))
            throw new MinioBucketNotFoundException(
                    String.format("Bucket type %s not found", attachmentType));

        var response = minioService.generatePreSignedUploadUrl(fileName, attachmentType, userOptional.get().getId());



        return Optional.ofNullable(response)
                .orElseThrow(() -> new PreSignedUrlGenerationException("Error while generating presigned URL"));
    }


    @Operation(summary = "Initiate download, get pre-signed URL to download file")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Pre-signed URL generated successfully", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = InitiateDownloadResponse.class))
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
    @SecurityRequirement(name = "BearerAuthentication")
    @GetMapping("/download/{fileName}/{attachmentType}")
    @ResponseStatus(HttpStatus.OK)
    public InitiateDownloadResponse initiateDownload(@Valid @FilenameValid @PathVariable String fileName,
                                                     @PathVariable String attachmentType,
                                                     @AuthenticationPrincipal User user) {
        var userOptional = userService.getUserByUsername(user.getUsername());

        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(
                    String.format("User with username %s not found", user.getUsername()));
        }

        if (!minioService.isBucketExists(attachmentType)) {
            throw new MinioBucketNotFoundException(
                    String.format("Bucket type %s not found", attachmentType));
        }

        var response = minioService.generatePreSignedDownloadUrl(fileName, attachmentType, userOptional.get().getId());

        return Optional.ofNullable(response)
                .orElseThrow(() -> new PreSignedUrlGenerationException("Error while generating presigned URL"));
    }


//    @Operation(summary = "Upload file to Minio storage")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "File uploaded successfully", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = BinaryUploadResponse.class))
//            }),
//            @ApiResponse(responseCode = "400", description = "Invalid presigned URL", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
//            }),
//            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
//                    @Content(mediaType = "application/json" , schema = @Schema(implementation = ErrorResponse.class))
//            }),
//            @ApiResponse(responseCode = "500", description = "Error while uploading file", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
//            })
//    })
//    @SecurityRequirement(name = "BearerAuthentication")
//    @PostMapping(value = "/upload", consumes = "multipart/form-data")
//    @ResponseStatus(HttpStatus.OK)
//    public BinaryUploadResponse uploadFile(@PreSignedURLValid @RequestParam("url") String preSignedURL, @RequestParam MultipartFile file, @AuthenticationPrincipal User user) {
//
//        var userOptional = userService.getUserByUsername(user.getUsername());
//
//        if (userOptional.isEmpty()) {
//            throw new UserNotFoundException(
//                    String.format("User with username %s not found", user.getUsername()));
//        }
//
//        return minioService.uploadFile(preSignedURL, file);
//    }
//
//    @Operation(summary = "Download file from Minio storage")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "File downloaded successfully", content = {
//                    @Content(mediaType = "application/octet-stream", schema = @Schema(implementation = BinaryUploadResponse.class))
//            }),
//            @ApiResponse(responseCode = "400", description = "Invalid presigned URL", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
//            }),
//            @ApiResponse(responseCode = "403", description = "Forbidden", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))
//            }),
//            @ApiResponse(responseCode = "500", description = "Error while downloading file", content = {
//                    @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorMessage.class))
//            })
//    })
//    @SecurityRequirement(name = "BearerAuthentication")
//    @GetMapping(value = "/download", consumes = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//    @ResponseStatus(HttpStatus.OK)
//    public BinaryDownloadResponse downloadFile( @PreSignedURLValid @RequestParam String preSignedURL, @AuthenticationPrincipal User user) {
//        var userOptional = userService.getUserByUsername(user.getUsername());
//
//        if (userOptional.isEmpty()) {
//            throw new UserNotFoundException(
//                    String.format("User with username %s not found", user.getUsername()));
//        }
//
//        return minioService.downloadFile(preSignedURL);
//    }


}
