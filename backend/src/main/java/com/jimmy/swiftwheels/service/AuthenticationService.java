package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.token.Token;
import com.jimmy.swiftwheels.token.TokenRepository;
import com.jimmy.swiftwheels.token.TokenType;
import com.jimmy.swiftwheels.user.Role;
import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;
import com.jimmy.swiftwheels.util.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public ResponseEntity<AuthenticationResponse> register(RegisterRequest request) {
        // validate credentials
        if(request == null
                || request.getUsername() == null || request.getUsername().isEmpty()
                || request.getPassword() == null || request.getPassword().isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(AuthenticationResponse.builder().message(ResponseMessage.INVALID_CREDENTIALS).build());
        }

        // check if username is not taken already
        if(userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(AuthenticationResponse.builder().message(ResponseMessage.USERNAME_EXISTS).build());
        }

        // save user to database and generate token
        var user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();
        var savedUser = userRepository.save(user);
        var jwtToken = jwtService.generateToken(user);
        saveUserToken(savedUser, jwtToken);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .username(user.getUsername())
                .Role(user.getRole().name())
                .token(jwtToken)
                .message(ResponseMessage.REGISTRATION_SUCCESSFUL)
                .build());
    }

    public ResponseEntity<AuthenticationResponse> authenticate(AuthenticationRequest request) {
        // authenticate users credentials
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(AuthenticationResponse.builder().message(exception.getMessage()).build());
        }

        // get user
        var user = userRepository.findByUsername(request.getUsername()).orElse(null);
        if(user == null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(AuthenticationResponse.builder().message(ResponseMessage.INVALID_CREDENTIALS).build());
        }

        // generate token
        var jwtToken = jwtService.generateToken(user);
        revokeAllUserTokens(user);
        saveUserToken(user, jwtToken);

        return ResponseEntity.ok(AuthenticationResponse.builder()
                .username(user.getUsername())
                .Role(user.getRole().name())
                .token(jwtToken)
                .message(ResponseMessage.LOGIN_SUCCESSFUL)
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
    }

    private void revokeAllUserTokens(User user) {
        var validUserTokens = tokenRepository.findAllValidTokenByUser(user.getId());
        if (validUserTokens.isEmpty()) {
            return;
        }
        validUserTokens.forEach(token -> {
            token.setRevoked(true);
            token.setExpired(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }
}
