package com.jimmy.swiftwheels.util.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EditVehicleRequest {
    private Integer vehicle_id;
    private String vehicle_name;
    private String vehicle_type;
    private double latitude;
    private double longitude;
}
