package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.util.AuthorizationResponse;
import com.jimmy.swiftwheels.util.Message;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/for/")
public class AuthorizationController {
    @PostMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AuthorizationResponse> forUser() {
        return ResponseEntity.ok(AuthorizationResponse.builder().message(Message.AUTHORIZED).build());
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorizationResponse> forAdmin() {
        return ResponseEntity.ok(AuthorizationResponse.builder().message(Message.AUTHORIZED).build());
    }
}
