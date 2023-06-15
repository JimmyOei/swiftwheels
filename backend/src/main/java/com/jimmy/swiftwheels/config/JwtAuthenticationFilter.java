package com.jimmy.swiftwheels.config;

import com.jimmy.swiftwheels.token.TokenRepository;
import com.jimmy.swiftwheels.service.JwtService;

import java.io.IOException;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    protected void doFilterInternal(@NonNull javax.servlet.http.HttpServletRequest request,
                                    @NonNull javax.servlet.http.HttpServletResponse response,
                                    @NonNull javax.servlet.FilterChain filterChain) throws javax.servlet.ServletException, IOException {
        logger.debug("Received request: URI={}, Method={}, RemoteAddr={}, Headers={}",
                request.getRequestURI(), request.getMethod(), request.getRemoteAddr(), request.getHeaderNames());

        if(request.getServletPath().contains("/api/auth")
                || request.getServletPath().contains("/api/vehicle/bounds")
                || request.getServletPath().contains("/api/vehicle/available")) {
            logger.debug("Request to unauthorized url");
            filterChain.doFilter(request, response);
            return;
        }
        logger.debug("Request to authorized url");

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        if(authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7);      // length of "Bearer " is 7
        username = jwtService.extractUsername(jwt);
        if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            var isTokenValid = tokenRepository.findByToken(jwt)
                    .map(t -> !t.isRevoked() && !t.isExpired())
                    .orElse(false);
            if(jwtService.isTokenValid(jwt, userDetails.getUsername()) && isTokenValid) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails((javax.servlet.http.HttpServletRequest) request)
                );
                SecurityContextHolder.getContext().setAuthentication(authToken);
                logger.debug("User with username=\"{}\" is authorized", username);
            }
            logger.debug("User with username=\"{}\" is not authorized", username);
        }
        filterChain.doFilter(request, response);
    }
}
