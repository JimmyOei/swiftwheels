package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.util.ResponseMessage;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthorizationController {
    @PostMapping("/forUser")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<String> forUser() {
        return ResponseEntity.ok(ResponseMessage.AUTHORIZED);
    }

    @PostMapping("/forAdmin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> forAdmin() {
        return ResponseEntity.ok(ResponseMessage.AUTHORIZED);
    }
}
