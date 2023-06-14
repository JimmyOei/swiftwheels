package com.jimmy.swiftwheels.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VehicleRequest {

    private String token;

    private Integer vehicle_id;
}