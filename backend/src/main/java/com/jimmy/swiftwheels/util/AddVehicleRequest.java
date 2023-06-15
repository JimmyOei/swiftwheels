package com.jimmy.swiftwheels.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddVehicleRequest {

    private String vehicle_name;

    private String vehicle_type;

    private double latitude;

    private double longitude;
}
