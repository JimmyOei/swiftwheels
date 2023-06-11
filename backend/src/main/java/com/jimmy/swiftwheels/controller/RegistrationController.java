package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody User user) {
        /* Validate username */
        if(user.getUsername() == null || user.getUsername().isEmpty() || user.getUsername().length() > 15) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("register_failed_username");
        }

        /* Validate password */
        if(user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().length() > 20) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("register_failed_password");
        }

        /* Validate unique username */
        if(!userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("register_failed_exists");
        }

        /* Encrypt password */
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        /* Save user to database */
        userRepository.save(user);

        return ResponseEntity.ok("register_success");
    }
}

