package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.logger.LoggerActionConstants;
import com.jimmy.swiftwheels.logger.LoggerMessage;
import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;
import com.jimmy.swiftwheels.util.request.AddVehicleRequest;
import com.jimmy.swiftwheels.util.request.DeleteVehicleRequest;
import com.jimmy.swiftwheels.util.request.EditVehicleRequest;
import com.jimmy.swiftwheels.util.request.VehicleRequest;
import com.jimmy.swiftwheels.util.response.MessageResponse;
import com.jimmy.swiftwheels.vehicle.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private boolean outOfBoundaries(double longitude, double latitude) {
        return (longitude > VehicleLocationBounds.MAX_LONGITUDE || longitude < VehicleLocationBounds.MIN_LONGITUDE
                || latitude > VehicleLocationBounds.MAX_LATITUDE || latitude < VehicleLocationBounds.MIN_LATITUDE);
    }

    public ResponseEntity<List<Vehicle>> getAllAvailableVehicles() {
        return ResponseEntity.ok(vehicleRepository.findAllAvailableVehicles());
    }

    public ResponseEntity<MessageResponse> addVehicle(AddVehicleRequest request) {
        if(outOfBoundaries(request.getLongitude(), request.getLatitude())) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.ADD, "Location is out of boundaries");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        if(request.getVehicle_name() == null || request.getVehicle_name().isEmpty()) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.ADD, "Vehicle name is invalid");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        VehicleType type;
        try {
            type = VehicleType.valueOf(request.getVehicle_type());
        } catch (IllegalArgumentException e) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.ADD,
                    String.format("Type '%s' is not a valid type", request.getVehicle_type()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        Vehicle vehicle = Vehicle.builder()
                .name(request.getVehicle_name())
                .type(type)
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .available(true)
                .build();
        vehicleRepository.save(vehicle);
        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.ADD) +
                String.format(": id='%s', name='%s', type='%s', latitude='%s', longitude='%s', available='%s'",
                        vehicle.getId(), vehicle.getName(), vehicle.getType().toString(), vehicle.getLatitude(),
                        vehicle.getLongitude(), vehicle.isAvailable());
        logger.info(message);
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    public ResponseEntity<MessageResponse> reserveVehicle(VehicleRequest request) {
        if(request.getToken() == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RESERVATION, "Token is empty");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        if(request.getVehicle_id() == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RESERVATION, "Vehicle id is empty");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }
        Vehicle vehicle = vehicleRepository.getReferenceById(request.getVehicle_id());
        if(!vehicle.isAvailable()) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RESERVATION,
                    String.format("Vehicle id '%s' is already reserved", request.getVehicle_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        String username = jwtService.extractUsername(request.getToken());
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RESERVATION, "Cannot find user");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }
        if(user.getVehicle() != null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RESERVATION,
                    String.format("User '%s' is already reserving vehicle id '%s'", username, request.getVehicle_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        vehicle.setAvailable(false);
        vehicle.setUser(user);
        vehicleRepository.save(vehicle);
        user.setVehicle(vehicle);
        userRepository.save(user);

        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.RESERVATION)
                + String.format(": User '%s' reserved vehicle id '%s'", username, request.getVehicle_id());
        logger.info(message);
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    public ResponseEntity<MessageResponse> releaseVehicle(VehicleRequest request) {
        if(request.getToken() == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RELEASE, "Token is empty");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        if(request.getVehicle_id() == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RELEASE, "Vehicle id is empty");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }
        Vehicle vehicle = vehicleRepository.getReferenceById(request.getVehicle_id());
        if(vehicle.isAvailable()) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RELEASE,
                    String.format("Vehicle id '%s' is not reserved", request.getVehicle_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        String username = jwtService.extractUsername(request.getToken());
        User user = userRepository.findByUsername(username).orElse(null);
        if(user == null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RELEASE, "Cannot find user");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }
        if(user.getVehicle() != vehicle) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.RELEASE,
                    String.format("User '%s' is not reserving vehicle id '%s'", username, request.getVehicle_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        vehicle.setAvailable(true);
        vehicle.setUser(null);
        vehicleRepository.save(vehicle);
        user.setVehicle(null);
        userRepository.save(user);

        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.RELEASE)
                + String.format(": User '%s' released vehicle id '%s'", username, request.getVehicle_id());
        logger.info(message);
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    public ResponseEntity<List<VehicleDTO>> getAllVehicles() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<VehicleDTO> vehicleDTOs = vehicles.stream().map(VehicleDTO::new).collect(Collectors.toList());
        return ResponseEntity.ok(vehicleDTOs);
    }

    public ResponseEntity<MessageResponse> deleteVehicle(DeleteVehicleRequest request) {
        if(!vehicleRepository.existsById(request.getVehicle_id())) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.DELETE,
                    String.format("Vehicle id '%s' does not exist", request.getVehicle_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        Vehicle vehicle = vehicleRepository.getReferenceById(request.getVehicle_id());
        User user = vehicle.getUser();
        if(user != null) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.DELETE,
                    String.format("Vehicle id '%s' is being reserved", request.getVehicle_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        vehicleRepository.delete(vehicle);

        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.DELETE)
                + String.format(": id='%s', name='%s', type='%s', latitude='%s', longitude='%s'",
                vehicle.getId(), vehicle.getName(), vehicle.getType().toString(), vehicle.getLatitude(), vehicle.getLongitude());
        logger.info(message);
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }

    public ResponseEntity<MessageResponse> editVehicle(EditVehicleRequest vehicle) {
        if(!vehicleRepository.existsById(vehicle.getVehicle_id())) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.EDIT,
                    String.format("Vehicle id '%s' does not exist", vehicle.getVehicle_id()));
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }
        if(outOfBoundaries(vehicle.getLongitude(), vehicle.getLatitude())) {
            String message = LoggerMessage.getFailureMessage(LoggerActionConstants.EDIT, "Location is out of boundaries");
            logger.debug(message);
            return ResponseEntity.badRequest().body(MessageResponse.builder().message(message).build());
        }

        Vehicle origin_vehicle = vehicleRepository.getReferenceById(vehicle.getVehicle_id());
        origin_vehicle.setName(vehicle.getVehicle_name());
        origin_vehicle.setType(VehicleType.valueOf(vehicle.getVehicle_type()));
        origin_vehicle.setLatitude(vehicle.getLatitude());
        origin_vehicle.setLongitude(vehicle.getLongitude());

        vehicleRepository.save(origin_vehicle);

        String message = LoggerMessage.getSuccessMessage(LoggerActionConstants.EDIT)
                + String.format(": id='%s', name='%s', type='%s', latitude='%s', longitude='%s'",
                origin_vehicle.getId(), origin_vehicle.getName(), origin_vehicle.getType().toString(),
                origin_vehicle.getLatitude(), origin_vehicle.getLongitude());
        logger.info(message);
        return ResponseEntity.ok(MessageResponse.builder().message(message).build());
    }
}
