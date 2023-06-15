package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.logger.LoggerActionConstants;
import com.jimmy.swiftwheels.logger.LoggerMessage;
import com.jimmy.swiftwheels.token.Token;
import com.jimmy.swiftwheels.token.TokenRepository;
import com.jimmy.swiftwheels.token.TokenType;
import com.jimmy.swiftwheels.user.Role;
import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;

import com.jimmy.swiftwheels.util.request.AuthenticationRequest;
import com.jimmy.swiftwheels.util.request.LogoutRequest;
import com.jimmy.swiftwheels.util.request.RegisterRequest;
import com.jimmy.swiftwheels.util.response.AuthenticationResponse;
import com.jimmy.swiftwheels.util.response.MessageResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);

    public ResponseEntity<AuthenticationResponse> register(RegisterRequest request) {
        if(request == null
                || request.getUsername() == null || request.getUsername().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.REGISTRATION, "Invalid Credentials");
            logger.debug(message);
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message(message).build());
        }

        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.REGISTRATION, "Username Taken");
            logger.debug(message);
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message(message).build());
        }

        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.REGISTRATION);
        logger.info(message + String.format(": username='%s'", request.getUsername()));
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .username(user.getUsername())
                .Role(user.getRole().name())
                .token(jwtToken)
                .message(message)
                .build());
    }

    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (AuthenticationException exception) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.AUTHENTICATION, exception.getMessage());
            logger.debug(message);
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message(message).build());
        }

        var user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if(user == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.AUTHENTICATION,
                    String.format("User '%s' does not exist", request.getUsername()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(AuthenticationResponse.builder().message(message).build());
        }

        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.AUTHENTICATION);
        logger.info(message);
        return ResponseEntity.ok(AuthenticationResponse.builder()
                .username(user.getUsername())
                .Role(user.getRole().name())
                .token(jwtToken)
                .message(message)
                .build());
    }

    private void saveUserToken(User user, String jwtToken) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(TokenType.BEARER)
                .revoked(false)
                .build();
        tokenRepository.save(token);
        logger.debug("New token for user '{}' saved", user.getUsername());
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty()) {
            logger.error("User '{}' has no valid tokens, so could not revoke any", user.getUsername());
            return;
        }
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
        logger.info("All valid tokens for user '{}' revoked", user.getUsername());
    }

    public ResponseEntity<MessageResponse> logout(LogoutRequest request) {
        User user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if(user == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.LOGOUT,
                    String.format("User '%s' does not exist", request.getUsername()));
            logger.error(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        revokeAllUserTokens(user);
        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.LOGOUT);
        logger.info(message + String.format(": username='%s'", request.getUsername()));
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }
}
