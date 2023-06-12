package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.user.UserService;
import com.jimmy.swiftwheels.util.AuthenticationRequest;
import com.jimmy.swiftwheels.util.AuthenticationResponse;
import com.jimmy.swiftwheels.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    public AuthenticationResponse createJwtToken(AuthenticationRequest request) throws Exception {
        String username = request.getUsername();
        String password = request.getPassword();
        authenticate(username, password);

        UserDetails userDetails = userService.loadUserByUsername(username);
        String newGeneratedToken = jwtUtil.generateToken(userDetails);

        return new AuthenticationResponse(newGeneratedToken, username);
    }

    private void authenticate(String userName, String userPassword) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userName, userPassword));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
