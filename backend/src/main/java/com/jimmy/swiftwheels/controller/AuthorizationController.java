package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.logger.LoggerActionConstants;
import com.jimmy.swiftwheels.logger.LoggerMessage;
import com.jimmy.swiftwheels.util.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/for/")
public class AuthorizationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> forUser() {
        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.AUTHORIZATION);
        logger.info(message + String.format(": User '%s'", SecurityContextHolder.getContext().getAuthentication().getName()));
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    @PostMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> forAdmin() {
        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.AUTHORIZATION);
        logger.info(message + String.format(": Admin '%s'", SecurityContextHolder.getContext().getAuthentication().getName()));
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }
}
