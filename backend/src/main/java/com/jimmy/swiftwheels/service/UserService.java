package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.logger.LoggerActionConstants;
import com.jimmy.swiftwheels.logger.LoggerMessage;
import com.jimmy.swiftwheels.token.Token;
import com.jimmy.swiftwheels.token.TokenRepository;
import com.jimmy.swiftwheels.user.Role;
import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserDTO;
import com.jimmy.swiftwheels.user.UserRepository;
import com.jimmy.swiftwheels.util.request.DeleteUserRequest;
import com.jimmy.swiftwheels.util.request.EditUserRoleRequest;
import com.jimmy.swiftwheels.util.response.MessageResponse;
import com.jimmy.swiftwheels.util.response.ReservedResponse;
import com.jimmy.swiftwheels.vehicle.Vehicle;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public ResponseEntity<ReservedResponse> reservedVehicle(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            logger.debug("User '{}' does not exist", username);
            return ResponseEntity.badRequest().body(ReservedResponse.builder().build());
        }

        Vehicle vehicle = user.getVehicle();
        if(vehicle == null) {
            logger.debug("User {} is not reserving a vehicle", username);
            return ResponseEntity.ok(ReservedResponse.builder().vehicle_reserved(false).build());
        }

        logger.info("User {} is reserving a vehicle, namely vehicle id {}", username, vehicle.getId());
        return ResponseEntity.ok(ReservedResponse.builder()
                .vehicle_reserved(true)
                .vehicle_id(vehicle.getId())
                .vehicle_name(vehicle.getName())
                .build());
    }

    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    public ResponseEntity<MessageResponse> editUserRole(EditUserRoleRequest request) {
        User user = userRepository.findById(request.getUser_id()).orElse(null);
        if(user == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.EDIT,
                    String.format("User with id '%s' does not exist", request.getUser_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }


        String oldRole = user.getRole().toString();
        try {
            Role newRole = Role.valueOf(request.getRole());
            user.setRole(newRole);
            userRepository.save(user);
        } catch (IllegalArgumentException exception) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.EDIT,
                    String.format("Role '%s' does not exist", request.getRole()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.EDIT) +
                String.format(": changed role '%s' to '%s' for user '%s'", oldRole, request.getRole(), user.getUsername());
        logger.info(message);
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    public ResponseEntity<MessageResponse> deleteUser(DeleteUserRequest request) {
        User user = userRepository.findById(request.getUser_id()).orElse(null);
        if(user == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.DELETE,
                    String.format("User with id '%s' does not exist", request.getUser_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        if(user.getVehicle() != null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.DELETE,
                    String.format("User '%s' is reserving a vehicle ('id %s')", user.getUsername(), user.getVehicle().getId()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        List<Token> tokens = user.getTokens();
        if(tokens != null) {
            tokenRepository.deleteAll(tokens);
        }

        userRepository.delete(user);

        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.DELETE) +
                String.format(": delete user '%s'", user.getUsername());
        logger.info(message);
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }
}
