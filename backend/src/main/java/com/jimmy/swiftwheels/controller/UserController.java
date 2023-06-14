package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.service.UserService;
import com.jimmy.swiftwheels.util.ReservedResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/reserved")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReservedResponse> reservedVehicle(@RequestBody String username) {
        return userService.reservedVehicle(username);
    }
}
