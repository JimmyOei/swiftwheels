package com.jimmy.swiftwheels.util.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("username")
    private String username;

    @JsonProperty("role")
    private String Role;

    @JsonProperty("token")
    private String token;

    @JsonProperty("message")
    private String message;
}
