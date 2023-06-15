package com.jimmy.swiftwheels.controller;

import com.jimmy.swiftwheels.service.VehicleService;
import com.jimmy.swiftwheels.util.request.AddVehicleRequest;
import com.jimmy.swiftwheels.util.request.DeleteVehicleRequest;
import com.jimmy.swiftwheels.util.request.EditVehicleRequest;
import com.jimmy.swiftwheels.util.request.VehicleRequest;
import com.jimmy.swiftwheels.util.response.MessageResponse;
import com.jimmy.swiftwheels.vehicle.Vehicle;
import com.jimmy.swiftwheels.vehicle.VehicleDTO;
import com.jimmy.swiftwheels.vehicle.VehicleLocationBounds;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicle")
@AllArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;
    private static final Logger logger = LoggerFactory.getLogger(VehicleController.class);

    @GetMapping("/available")
    public ResponseEntity<List<Vehicle>> availableVehicles() {
        logger.info("Request to 'availableVehicles'");
        return vehicleService.getAllAvailableVehicles();
    }

    @GetMapping("/bounds")
    public ResponseEntity<Map<String, Double>> vehicleLocationBounds() {
        logger.info("Request to 'getDatabase'");
        return ResponseEntity.ok().body(VehicleLocationBounds.getBounds());
    }

    @PostMapping("/reserve")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> reserveVehicle(@RequestBody VehicleRequest request) {
        logger.info("Request to 'reserveVehicle' by user '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return vehicleService.reserveVehicle(request);
    }

    @PostMapping("/release")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<MessageResponse> releaseVehicle(@RequestBody VehicleRequest request) {
        logger.info("Request to 'releaseVehicle' by user '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return vehicleService.releaseVehicle(request);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> addVehicle(@RequestBody AddVehicleRequest request) {
        logger.info("Request to 'addVehicle' by user '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return vehicleService.addVehicle(request);
    }

    @PostMapping("/delete")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> deleteVehicle(@RequestBody DeleteVehicleRequest request) {
        logger.info("Request to 'deleteVehicle' by admin '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return vehicleService.deleteVehicle(request);
    }

    @PostMapping("/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> editVehicle(@RequestBody EditVehicleRequest vehicle) {
        logger.info("Request to 'editVehicle' by admin '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return vehicleService.editVehicle(vehicle);
    }

    @GetMapping("/database")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VehicleDTO>> getDatabase() {
        logger.info("Request to 'getDatabase' by admin '{}'",
                SecurityContextHolder.getContext().getAuthentication().getName());
        return vehicleService.getAllVehicles();
    }
}
