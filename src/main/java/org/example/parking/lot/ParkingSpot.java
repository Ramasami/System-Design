package org.example.parking.lot;

import lombok.Data;

import java.util.Objects;

@Data
public class ParkingSpot {
    private final VehicleType vehicleType;
    private final int spotNumber;
    private final int levelNumber;

    private Vehicle vehicle;
    private boolean isAvailable;

    public ParkingSpot(VehicleType vehicleType, int spotNumber, int levelNumber) {
        this.vehicleType = vehicleType;
        this.spotNumber = spotNumber;
        this.levelNumber = levelNumber;
        this.isAvailable = true;
    }

    public synchronized boolean reserve(Vehicle vehicle) {
        if (isAvailable) {
            this.isAvailable = false;
            this.vehicle = vehicle;
            return true;
        } else {
            return false;
        }
    }

    public synchronized boolean unReserve() {
        if (!isAvailable) {
            this.isAvailable = true;
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ParkingSpot that)) return false;
        return spotNumber == that.spotNumber && levelNumber == that.levelNumber && vehicleType == that.vehicleType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleType, spotNumber, levelNumber);
    }
}
