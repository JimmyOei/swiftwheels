package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;
import com.jimmy.swiftwheels.util.*;
import com.jimmy.swiftwheels.vehicle.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    private final UserRepository userRepository;

    private final JwtService jwtService;

    private boolean outOfBoundaries(double longitude, double latitude) {
        return (longitude > VehicleLocationBounds.MAX_LONGITUDE || longitude < VehicleLocationBounds.MIN_LONGITUDE
                || latitude > VehicleLocationBounds.MAX_LATITUDE || latitude < VehicleLocationBounds.MIN_LATITUDE);
    }

    public ResponseEntity<List<Vehicle>> getAllAvailableVehicles() {
        return ResponseEntity.ok(vehicleRepository.findAllAvailableVehicles());
    }

    public ResponseEntity<MessageResponse> addVehicle(AddVehicleRequest request) {
        System.out.println("req: "+ request);
        if(outOfBoundaries(request.getLongitude(), request.getLatitude())) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(Message.OUT_OF_BOUNDS_LOCATION).build());
        }

        if(request.getVehicle_name() == null || request.getVehicle_name().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(
                            Message.INVALID_VEHICLE_PROPERTIES).build());
        }

        VehicleType type;
        try {
            type = VehicleType.valueOf(request.getVehicle_type());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    MessageResponse.builder().message(
                            Message.INVALID_VEHICLE_PROPERTIES).build());
        }

        Vehicle vehicle = Vehicle.builder()
                .name(request.getVehicle_name())
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

    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleDTO> vehicleDTOs = vehicles.stream().map(VehicleDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(vehicleDTOs);
    }

    public ResponseEntity<MessageResponse> deleteVehicle(DeleteVehicleRequest request) {
        if(!vehicleRepository.existsById(request.getVehicle_id())) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(Message.VEHICLE_NOT_EXISTS).build());
        }

        Vehicle vehicle = vehicleRepository.getReferenceById(request.getVehicle_id());
        User user = vehicle.getUser();
        if(user != null) {
            user.setVehicle(null);
            userRepository.save(user);
        }

        vehicleRepository.delete(vehicle);
        return ResponseEntity.ok(MessageResponse.builder().message(Message.DELETE_SUCCESSFUL).build());
    }

    public ResponseEntity<MessageResponse> editVehicle(EditVehicleRequest vehicle) {
        if(!vehicleRepository.existsById(vehicle.getVehicle_id())) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(Message.VEHICLE_NOT_EXISTS).build());
        }
        if(outOfBoundaries(vehicle.getLongitude(), vehicle.getLatitude())) {
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(Message.OUT_OF_BOUNDS_LOCATION).build());
        }

        Vehicle origin_vehicle = vehicleRepository.getReferenceById(vehicle.getVehicle_id());
        origin_vehicle.setName(vehicle.getVehicle_name());
        origin_vehicle.setType(VehicleType.valueOf(vehicle.getVehicle_type()));
        origin_vehicle.setLatitude(vehicle.getLatitude());
        origin_vehicle.setLongitude(vehicle.getLongitude());

        vehicleRepository.save(origin_vehicle);
        return ResponseEntity.ok(MessageResponse.builder().message(Message.EDIT_SUCCESSFUL).build());
    }
}
