package com.jimmy.swiftwheels.user;

import com.jimmy.swiftwheels.vehicle.Vehicle;
import com.jimmy.swiftwheels.vehicle.VehicleType;
import lombok.Data;

import javax.persistence.criteria.CriteriaBuilder;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String role;
    private Integer reserved_vehicle_id;

    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.role = user.getRole().toString();
        this.reserved_vehicle_id = user.getVehicle() != null ? user.getVehicle().getId() : null;
    }
}

