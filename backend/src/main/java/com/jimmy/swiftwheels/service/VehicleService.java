package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;
import com.jimmy.swiftwheels.util.ResponseMessage;
import com.jimmy.swiftwheels.util.addVehicleRequest;
import com.jimmy.swiftwheels.util.reserveVehicleRequest;
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

    public ResponseEntity<String> addVehicle(addVehicleRequest request) {
        System.out.println("request: " + request);
        if(request.getLongitude() > VehicleLocationBounds.MAX_LONGITUDE || request.getLongitude() < VehicleLocationBounds.MIN_LONGITUDE
            && request.getLatitude() > VehicleLocationBounds.MAX_LATITUDE || request.getLatitude() < VehicleLocationBounds.MIN_LATITUDE) {
            return ResponseEntity.badRequest().body(ResponseMessage.OUT_OF_BOUNDS_LOCATION);
        }

        if(request.getName() == null || request.getName().isEmpty()) {
            return ResponseEntity.badRequest().body(ResponseMessage.INVALID_VEHICLE_PROPERTIES);
        }

        VehicleType type;
        try {
            type = VehicleType.valueOf(request.getType());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(ResponseMessage.INVALID_VEHICLE_PROPERTIES);
        }

        Vehicle vehicle = Vehicle.builder()
                .name(request.getName())
                .type(type)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .available(true)
                .build();
        vehicleRepository.save(vehicle);
        return ResponseEntity.ok(ResponseMessage.ADD_VEHICLE_SUCCESSFUL);
    }

    public ResponseEntity<String> reserveVehicle(reserveVehicleRequest request) {
        if(request.getToken() == null || request.getUsername() == null ||
                !jwtService.isTokenValid(request.getToken(), request.getUsername())) {
            return ResponseEntity.badRequest().body(ResponseMessage.UNAUTHORIZED);
        }

        // check if vehicle is reserved
        if(request.getVehicle_id() == null) {
            return ResponseEntity.badRequest().body(ResponseMessage.INVALID_VEHICLE_PROPERTIES);
        }
        Vehicle vehicle = vehicleRepository.getReferenceById(request.getVehicle_id());
        if(!vehicle.isAvailable()) {
            return ResponseEntity.badRequest().body(ResponseMessage.VEHICLE_UNAVAILABLE);
        }

        // check if user is reserving a vehicle
        User user = userRepository.findByUsername(request.getUsername())
                .orElse(null);
        if(user == null) {
            return ResponseEntity.badRequest().body(ResponseMessage.INVALID_CREDENTIALS);
        }
        if(user.getVehicle() != null) {
            return ResponseEntity.badRequest().body(ResponseMessage.USER_ALREADY_HAS_VEHICLE);
        }

        vehicle.setAvailable(false);
        vehicle.setUser(user);
        vehicleRepository.save(vehicle);
        user.setVehicle(vehicle);
        userRepository.save(user);

        return ResponseEntity.ok(ResponseMessage.VEHICLE_RESERVE_SUCCESSFUL);
    }

}
