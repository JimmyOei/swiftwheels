package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.service.VehicleService;
import com.jimmy.swiftwheels.util.MessageResponse;
import com.jimmy.swiftwheels.util.addVehicleRequest;
import com.jimmy.swiftwheels.util.VehicleRequest;
import com.jimmy.swiftwheels.vehicle.Vehicle;
import com.jimmy.swiftwheels.vehicle.VehicleLocationBounds;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@AllArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;


    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> availableVehicles() {
        return vehicleService.getAllAvailableVehicles();
    }

    @GetMapping("/bounds")
    public ResponseEntity<Map<String, Double>> vehicleLocationBounds() {
        return ResponseEntity.ok().body(VehicleLocationBounds.getBounds());
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addVehicle(@RequestBody addVehicleRequest request) {
        return vehicleService.addVehicle(request);
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> reserveVehicle(@RequestBody VehicleRequest request) {
        return vehicleService.reserveVehicle(request);
    }

    @PostMapping("/release")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> releaseVehicle(@RequestBody VehicleRequest request) {
        return vehicleService.releaseVehicle(request);
    }
}
