package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.service.AuthenticationService;

import com.jimmy.swiftwheels.util.request.AuthenticationRequest;
import com.jimmy.swiftwheels.util.request.LogoutRequest;
import com.jimmy.swiftwheels.util.request.RegisterRequest;
import com.jimmy.swiftwheels.util.response.AuthenticationResponse;
import com.jimmy.swiftwheels.util.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final AuthenticationService authService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request) {
        logger.info("Request to 'register' for user '{}'", request.getUsername());
        return authService.register(request);
    }
    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        logger.info("Request to 'authenticate' by user '{}'", request.getUsername());
        return authService.authenticate(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<MessageResponse> logout(@RequestBody LogoutRequest request) {
        logger.info("Request to 'logout' by user '{}'", request.getUsername());
        return authService.logout(request);
    }
}