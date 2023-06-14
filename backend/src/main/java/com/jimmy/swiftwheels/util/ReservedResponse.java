package com.jimmy.swiftwheels.util;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReservedResponse {
    @JsonProperty("vehicle_reserved")
    private Boolean vehicle_reserved;

    @JsonProperty("vehicle_name")
    private String vehicle_name;

    @JsonProperty("vehicle_id")
    private Integer vehicle_id;
}
