package com.jimmy.swiftwheels.vehicle;

import java.util.HashMap;
import java.util.Map;

public final class VehicleLocationBounds {
    public static final double MAX_LONGITUDE = 4.594;
    public static final double MIN_LONGITUDE = 4.198868;
    public static final double MAX_LATITUDE = 52.120474;
    public static final double MIN_LATITUDE = 51.951484;

    public static Map<String, Double> getBounds() {
        Map<String, Double> bounds = new HashMap<>();
        bounds.put("max_longitude", MAX_LONGITUDE);
        bounds.put("min_longitude", MIN_LONGITUDE);
        bounds.put("max_latitude", MAX_LATITUDE);
        bounds.put("min_latitude", MIN_LATITUDE);
        return bounds;
    }
}
