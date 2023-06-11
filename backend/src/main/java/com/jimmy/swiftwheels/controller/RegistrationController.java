package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RegistrationController {
    @Autowired
    final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/register")
    public ResponseEntity<Object> registerUser(@RequestBody User user) {
        /* Validate username */
        if(user.getUsername() == null || user.getUsername().isEmpty() || user.getUsername().length() > 15) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"status\": 1, \"message\": \"Invalid username\"}");
        }

        /* Validate password */
        if(user.getPassword() == null || user.getPassword().isEmpty() || user.getPassword().length() > 20) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"status\": 1, \"message\": \"Invalid password\"}");
        }

        /* Validate unique username */
        if(userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("{\"status\": 1, \"message\": \"Username already exists\"}");
        }

        /* Encrypt password */
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        /* Save user to database */
        userRepository.save(user);

        return ResponseEntity.ok("{\"status\": 0, \"message\": \"Registration successful\"}");
    }
}

