package org.storage.biometrics.storagemimoio.auth.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.storage.biometrics.storagemimoio.auth.dtos.RegisterUserDto;
import org.storage.biometrics.storagemimoio.auth.dtos.LoginUserDto;
import org.storage.biometrics.storagemimoio.auth.entities.User;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.storage.biometrics.storagemimoio.auth.exceptions.UserAlreadyExistsException;
import org.storage.biometrics.storagemimoio.auth.exceptions.UserDoesNotExistException;
import org.storage.biometrics.storagemimoio.auth.responses.LoginResponse;
import org.storage.biometrics.storagemimoio.auth.services.AuthenticationService;
import org.storage.biometrics.storagemimoio.auth.services.JwtService;

import java.util.Optional;


@RestController
@RequestMapping("/api/v3/auth")
public class AuthenticationController {
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    public AuthenticationController(JwtService jwtService, AuthenticationService authenticationService) {
        this.jwtService = jwtService;
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "Register a new user")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User registered successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody RegisterUserDto registerUserDto) {
        Optional<User> existingUser = authenticationService.findUserByUsername(registerUserDto.username());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException(String.format("User with username %s already exists", registerUserDto.username()));
        }

        User registeredUser = authenticationService.signup(registerUserDto);
        return ResponseEntity.ok(registeredUser);
    }

    @Operation(summary = "Authenticate a user")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "User authenticated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid input")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody LoginUserDto loginUserDto) {
        Optional<User> authenticatedUser = Optional.ofNullable(authenticationService.authenticate(loginUserDto));

        if (authenticatedUser.isEmpty()) {
            throw new UserDoesNotExistException(String.format("User with username %s does not exist", loginUserDto.username()));
        }

        String jwtToken = jwtService.generateToken(authenticatedUser.get());

        LoginResponse loginResponse = new LoginResponse().setToken(jwtToken).setExpiresIn(jwtService.getExpirationTime());

        return ResponseEntity.ok(loginResponse);
    }
}