package com.jimmy.swiftwheels.logger;

public final class LoggerMessage {

//    /* Authentication messages */
//    public static final String REGISTRATION_SUCCESSFUL = "Registration successful";
//    public static final String USERNAME_EXISTS = "Username already exists";
//
//    public static final String USER_NOT_EXISTS = "User does not exists";
//    public static final String INVALID_CREDENTIALS = "Invalid credentials";
//    public static final String LOGIN_SUCCESSFUL = "Login successful";
//    public static final String AUTHORIZED = "Authorized";
//    public static final String LOGOUT_SUCCESSFUL = "Logout successful";
//    public static final String UNAUTHORIZED = "Unauthorized";
//
//    /* Role */
//    public static final String ROLE_NOT_EXISTS = "Role does not exists";
//    public static final String ROLE_CHANGE_SUCCESSFUL = "Role change successful";
//
//    /* Vehicle messages */
//    public static final String OUT_OF_BOUNDS_LOCATION = "Location is out of bounds";
//
//    public static final String ADD_VEHICLE_SUCCESSFUL = "Vehicle successfully added";
//    public static final String INVALID_VEHICLE_PROPERTIES = "Invalid vehicle properties";
//    public static final String VEHICLE_UNAVAILABLE = "Vehicle is unavailable";
//    public static final String USER_ALREADY_HAS_VEHICLE = "User is already reserving a vehicle";
//    public static final String VEHICLE_RESERVE_SUCCESSFUL = "Vehicle successfully reserved";
//
//    public static final String VEHICLE_RELEASE_SUCCESSFUL = "Vehicle successfully released";
//    public static final String NOT_YOUR_VEHICLE = "You are not reserving this vehicle";
//
//    public static final String DELETE_SUCCESSFUL = "Vehicle deleted successfully";
//
//    public static final String VEHICLE_NOT_EXISTS = "Vehicle does not exist";
//
//    public static final String EDIT_SUCCESSFUL = "Vehicle edited successfully";
//
//    public static final String CANNOT_EDIT_RESERVATIONS = "You cannot edit a vehicle's reservation user";
//    public static final String USER_RESERVING_VEHICLE = "User is reserving a vehicle";
//
//    /* User */
//    public static final String USER_DELETE_SUCCESSFUL = "User successfully deleted";

    public static String getFailureMessage(String action, String reason) {
        return String.format("%s failed: %s", action, reason);
    }

    public static String getSuccessMessage(String action) {
        return String.format("%s successful", action);
    }
}