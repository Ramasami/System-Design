package org.example.parking.lot;

import lombok.Data;

import java.util.Objects;

@Data
public class Vehicle {
    private final VehicleType vehicleType;
    private final int vehicleNumber;
    private ParkingSpot parkingSpot;

    public Vehicle(VehicleType vehicleType, int vehicleNumber) {
        this.vehicleType = vehicleType;
        this.vehicleNumber = vehicleNumber;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vehicle vehicle)) return false;
        return vehicleNumber == vehicle.vehicleNumber && vehicleType == vehicle.vehicleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleType, vehicleNumber);
    }
}
