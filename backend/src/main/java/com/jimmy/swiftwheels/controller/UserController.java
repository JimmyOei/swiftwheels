package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.service.UserService;
import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserDTO;
import com.jimmy.swiftwheels.util.*;
import com.jimmy.swiftwheels.vehicle.VehicleDTO;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteVehicle(@RequestBody DeleteUserRequest request) {
        return userService.deleteUser(request);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> editVehicle(@RequestBody EditUserRoleRequest request) {
        return userService.editUserRole(request);
    }

    @GetMapping("/database")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getDatabase() {
        return userService.getAllUsers();
    }
}
