package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.util.ResponseMessage;
import com.jimmy.swiftwheels.util.addVehicleRequest;
import com.jimmy.swiftwheels.vehicle.Vehicle;
import com.jimmy.swiftwheels.vehicle.VehicleLocationBounds;
import com.jimmy.swiftwheels.vehicle.VehicleRepository;
import com.jimmy.swiftwheels.vehicle.VehicleType;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.EnumSet;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleService {

    private final VehicleRepository vehicleRepository;

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
}
