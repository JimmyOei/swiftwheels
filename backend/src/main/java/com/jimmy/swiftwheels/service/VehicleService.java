package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;
import com.jimmy.swiftwheels.util.Message;
import com.jimmy.swiftwheels.util.MessageResponse;
import com.jimmy.swiftwheels.util.addVehicleRequest;
import com.jimmy.swiftwheels.util.VehicleRequest;
import com.jimmy.swiftwheels.vehicle.Vehicle;
import com.jimmy.swiftwheels.vehicle.VehicleLocationBounds;
import com.jimmy.swiftwheels.vehicle.VehicleRepository;
import com.jimmy.swiftwheels.vehicle.VehicleType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    public ResponseEntity<List<Vehicle>> getAllAvailableVehicles() {
        return ResponseEntity.ok(vehicleRepository.findAllAvailableVehicles());
    }

    public ResponseEntity<MessageResponse> addVehicle(addVehicleRequest request) {
        if(request.getLongitude() > VehicleLocationBounds.MAX_LONGITUDE || request.getLongitude() < VehicleLocationBounds.MIN_LONGITUDE
            && request.getLatitude() > VehicleLocationBounds.MAX_LATITUDE || request.getLatitude() < VehicleLocationBounds.MIN_LATITUDE) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.OUT_OF_BOUNDS_LOCATION).build());
        }

        if(request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(
                            Message.INVALID_VEHICLE_PROPERTIES).build());
        }

        VehicleType type;
        try {
            type = VehicleType.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(
                            Message.INVALID_VEHICLE_PROPERTIES).build());
        }

        Vehicle vehicle = Vehicle.builder()
                .name(request.getName())
                .type(type)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .available(true)
                .build();
        vehicleRepository.save(vehicle);
        return ResponseEntity.ok(
                MessageResponse.builder().message(
                        Message.ADD_VEHICLE_SUCCESSFUL).build());
    }

    public ResponseEntity<MessageResponse> reserveVehicle(VehicleRequest request) {
        if(request.getToken() == null) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.UNAUTHORIZED).build());
        }

        // check if vehicle is reserved
        if(request.getVehicle_id() == null) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.INVALID_VEHICLE_PROPERTIES).build());
        }
        Vehicle vehicle = vehicleRepository.getReferenceById(request.getVehicle_id());
        if(!vehicle.isAvailable()) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.VEHICLE_UNAVAILABLE).build());
        }

        // check if user is reserving a vehicle
        String username = jwtService.extractUsername(request.getToken());
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.INVALID_CREDENTIALS).build());
        }
        if(user.getVehicle() != null) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.USER_ALREADY_HAS_VEHICLE).build());
        }

        vehicle.setAvailable(false);
        vehicle.setUser(user);
        vehicleRepository.save(vehicle);
        user.setVehicle(vehicle);
        userRepository.save(user);

        return ResponseEntity.ok(
                MessageResponse.builder().message(Message.VEHICLE_RESERVE_SUCCESSFUL).build());
    }

    public ResponseEntity<MessageResponse> releaseVehicle(VehicleRequest request) {
        if(request.getToken() == null) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.UNAUTHORIZED).build());
        }

        // check if vehicle is reserved
        if(request.getVehicle_id() == null) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.INVALID_VEHICLE_PROPERTIES).build());
        }
        Vehicle vehicle = vehicleRepository.getReferenceById(request.getVehicle_id());
        if(vehicle.isAvailable()) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.NOT_YOUR_VEHICLE).build());
        }

        // check if user is reserving a vehicle
        String username = jwtService.extractUsername(request.getToken());
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.INVALID_CREDENTIALS).build());
        }
        if(user.getVehicle() != vehicle) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.NOT_YOUR_VEHICLE).build());
        }

        vehicle.setAvailable(true);
        vehicle.setUser(null);
        vehicleRepository.save(vehicle);
        user.setVehicle(null);
        userRepository.save(user);

        return ResponseEntity.ok(
                MessageResponse.builder().message(Message.VEHICLE_RELEASE_SUCCESSFUL).build());
    }
}
