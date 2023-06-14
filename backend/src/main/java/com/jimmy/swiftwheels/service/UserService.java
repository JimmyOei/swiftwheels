package com.jimmy.swiftwheels.service;

import com.jimmy.swiftwheels.user.User;
import com.jimmy.swiftwheels.user.UserRepository;
import com.jimmy.swiftwheels.util.ReservedResponse;
import com.jimmy.swiftwheels.vehicle.VehicleRepository;
import com.jimmy.swiftwheels.vehicle.Vehicle;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
}
