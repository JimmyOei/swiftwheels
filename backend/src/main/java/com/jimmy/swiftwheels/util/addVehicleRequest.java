package com.jimmy.swiftwheels.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class addVehicleRequest {

    private String name;

    private String type;

    private double latitude;

    private double longitude;
}
