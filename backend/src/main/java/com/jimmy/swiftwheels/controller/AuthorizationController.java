package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.util.AuthorizationResponse;
import com.jimmy.swiftwheels.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {
    @PostMapping("/forUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AuthorizationResponse> forUser() {
        return ResponseEntity.ok(AuthorizationResponse.builder().message(ResponseMessage.AUTHORIZED).build());
    }

    @PostMapping("/forAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AuthorizationResponse> forAdmin() {
        return ResponseEntity.ok(AuthorizationResponse.builder().message(ResponseMessage.AUTHORIZED).build());
    }
}
