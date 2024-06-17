package org.storage.biometrics.storagemimoio.auth.services;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.storage.biometrics.storagemimoio.auth.dtos.LoginUserDto;
import org.storage.biometrics.storagemimoio.auth.dtos.RegisterUserDto;
import org.storage.biometrics.storagemimoio.auth.entities.User;
import org.storage.biometrics.storagemimoio.auth.repositories.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(
            UserRepository userRepository,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User signup(RegisterUserDto input) {
        var user = new User(
                input.username(),
                passwordEncoder.encode(input.password())
        );

        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto input) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        input.username(),
                        input.password()
                )
        );

        return userRepository.findByUsername(input.username())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }


    public Optional<User> findUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
