package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.service.UserService;
import com.jimmy.swiftwheels.user.UserDTO;
import com.jimmy.swiftwheels.util.request.DeleteUserRequest;
import com.jimmy.swiftwheels.util.request.EditUserRoleRequest;
import com.jimmy.swiftwheels.util.response.MessageResponse;
import com.jimmy.swiftwheels.util.response.ReservedResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/user")
@AllArgsConstructor
public class UserController {

    private final UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @PostMapping("/reserved")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ReservedResponse> reservedVehicle(@RequestBody String username) {
        logger.info("Request to 'reservedVehicle' by user '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.reservedVehicle(username);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteVehicle(@RequestBody DeleteUserRequest request) {
        logger.info("Request to 'deleteVehicle' by admin '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.deleteUser(request);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> editVehicle(@RequestBody EditUserRoleRequest request) {
        logger.info("Request to 'editVehicle' by admin '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.editUserRole(request);
    }

    @GetMapping("/database")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getDatabase() {
        logger.info("Request to 'getDatabase' by admin '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return userService.getAllUsers();
    }
}
