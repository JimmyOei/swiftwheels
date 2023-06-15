package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.token.Token;
import com.jimmy.swiftwheels.token.TokenRepository;
import com.jimmy.swiftwheels.user.Role;
import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserDTO;
import com.jimmy.swiftwheels.user.UserRepository;
import com.jimmy.swiftwheels.util.*;
import com.jimmy.swiftwheels.vehicle.VehicleDTO;
import com.jimmy.swiftwheels.vehicle.VehicleRepository;
import com.jimmy.swiftwheels.vehicle.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final TokenRepository tokenRepository;

    public ResponseEntity<ReservedResponse> reservedVehicle(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            return ResponseEntity.badRequest().body(ReservedResponse.builder().build());
        }

        Vehicle vehicle = user.getVehicle();
        if(vehicle != null) {
            return ResponseEntity.ok(ReservedResponse.builder()
                    .vehicle_reserved(true)
                    .vehicle_id(vehicle.getId())
                    .vehicle_name(vehicle.getName())
                    .build());
        }

        return ResponseEntity.ok(ReservedResponse.builder().vehicle_reserved(false).build());
    }

    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream().map(UserDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    public ResponseEntity<MessageResponse> editUserRole(EditUserRoleRequest request) {
        User user = userRepository.findById(request.getUser_id()).orElse(null);
        if(user == null) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(Message.USER_NOT_EXISTS).build());
        }

        try {
            Role role = Role.valueOf(request.getRole());
            user.setRole(role);
            userRepository.save(user);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(Message.ROLE_NOT_EXISTS).build());
        }

        return ResponseEntity.ok(MessageResponse.builder().message(Message.ROLE_CHANGE_SUCCESSFUL).build());
    }

    public ResponseEntity<MessageResponse> deleteUser(DeleteUserRequest request) {
        User user = userRepository.findById(request.getUser_id()).orElse(null);
        if(user == null) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(Message.USER_NOT_EXISTS).build());
        }

        if(user.getVehicle() != null) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(Message.USER_RESERVING_VEHICLE).build());
        }

        List<Token> tokens = user.getTokens();
        if(tokens != null) {
            tokenRepository.deleteAll(tokens);
        }

        userRepository.delete(user);

        return ResponseEntity.ok(MessageResponse.builder().message(Message.USER_DELETE_SUCCESSFUL).build());
    }
}
