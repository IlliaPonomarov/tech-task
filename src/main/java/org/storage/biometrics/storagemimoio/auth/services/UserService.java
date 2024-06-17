package org.storage.biometrics.storagemimoio.auth.services;

import org.springframework.stereotype.Service;
import org.storage.biometrics.storagemimoio.auth.entities.User;
import org.storage.biometrics.storagemimoio.auth.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
}
