package com.jimmy.swiftwheels.vehicle;

import lombok.Data;

@Data
public class VehicleDTO {
    private Integer id;
    private String name;
    private VehicleType type;
    private Integer userId;
    private boolean available;
    private double latitude;
    private double longitude;

    public VehicleDTO(Vehicle vehicle) {
        this.id = vehicle.getId();
        this.name = vehicle.getName();
        this.type = vehicle.getType();
        this.userId = (vehicle.getUser() != null) ? vehicle.getUser().getId() : null;
        this.available = vehicle.isAvailable();
        this.latitude = vehicle.getLatitude();
        this.longitude = vehicle.getLongitude();
    }
}
