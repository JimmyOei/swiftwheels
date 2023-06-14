package com.jimmy.swiftwheels.vehicle;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Integer> {
    @Query(value = "SELECT v FROM Vehicle v WHERE v.available = true")
    List<Vehicle> findAllAvailableVehicles();
}
