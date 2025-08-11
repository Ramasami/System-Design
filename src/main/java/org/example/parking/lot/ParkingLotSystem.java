package org.example.parking.lot;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Logger;

/**
 * Manages a multi-level parking lot system with support for different vehicle types.
 */
public class ParkingLotSystem implements AutoCloseable {
    private static final Logger logger = Logger.getLogger(ParkingLotSystem.class.getName());
    private final List<Level> levels;
    private final int totalLevels;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    /**
     * Creates a new parking lot system with the specified number of levels
     *
     * @param totalLevels number of parking levels
     * @throws IllegalArgumentException if totalLevels is less than 1
     */
    public ParkingLotSystem(int totalLevels) {
        if (totalLevels < 1) {
            throw new IllegalArgumentException("Parking lot must have at least one level");
        }
        this.totalLevels = totalLevels;
        this.levels = Collections.synchronizedList(new ArrayList<>(totalLevels));
        initializeLevels();
    }

    private void initializeLevels() {
        try {
            lock.writeLock().lock();
            for (int i = 0; i < totalLevels; i++) {
                levels.add(new Level(i));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Adds a new parking spot to a specific level
     *
     * @param level       the level number
     * @param vehicleType type of vehicle the spot is for
     * @return true if spot was added successfully
     */
    public boolean addParkingSpot(int level, VehicleType vehicleType) {
        validateLevel(level);
        try {
            lock.writeLock().lock();
            levels.get(level).addParkingSpot(level, vehicleType);
            logger.info(() -> String.format("Added parking spot for %s at level %d", vehicleType, level));
            return true;
        } catch (Exception e) {
            logger.warning(() -> String.format("Failed to add parking spot: %s", e.getMessage()));
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Gets the total count of free parking spots for a vehicle type
     *
     * @param vehicleType type of vehicle
     * @return number of available spots
     */
    public int getFreeParkingSpotsCount(@NonNull VehicleType vehicleType) {
        try {
            lock.readLock().lock();
            return levels.stream()
                    .mapToInt(level -> level.getFreeParkingSpotsCount(vehicleType))
                    .sum();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Parks a vehicle in the first available spot
     *
     * @param vehicle the vehicle to park
     * @return true if vehicle was parked successfully
     */
    public boolean parkVehicle(@NonNull Vehicle vehicle) {
        try {
            lock.readLock().lock();
            for (Level level : levels) {
                if (level.getFreeParkingSpotsCount(vehicle.getVehicleType()) > 0) {
                    Optional<ParkingSpot> spot = level.reserveParkingSpot(vehicle.getVehicleType(), vehicle);
                    if (spot.isPresent()) {
                        vehicle.setParkingSpot(spot.get());
                        logger.info(() -> String.format("Vehicle %d parked at level %d, spot %d",
                                vehicle.getVehicleNumber(), level.getLevelNumber(), spot.get().getSpotNumber()));
                        return true;
                    }
                }
            }
            logger.warning(() -> String.format("No parking spot available for vehicle %d", vehicle.getVehicleNumber()));
            return false;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Removes a vehicle from its parking spot
     *
     * @param vehicle the vehicle to unpark
     * @return true if vehicle was unparked successfully
     */
    public boolean unParkVehicle(@NonNull Vehicle vehicle) {
        try {
            lock.writeLock().lock();
            if (vehicle.getParkingSpot() == null) {
                logger.warning(() -> String.format("Vehicle %d is not parked", vehicle.getVehicleNumber()));
                return false;
            } else {
                ParkingSpot spot = vehicle.getParkingSpot();
                Level level = levels.get(spot.getLevelNumber());
                if (level.unReserveParkingSpot(spot)) {
                    vehicle.setParkingSpot(null);
                    logger.info(() -> String.format("Vehicle %d unparked from level %d, spot %d",
                            vehicle.getVehicleNumber(), level.getLevelNumber(), spot.getSpotNumber()));
                    return true;
                }
                logger.warning(() -> String.format("Failed to unpark vehicle %d", vehicle.getVehicleNumber()));
                return false;
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void validateLevel(int level) {
        if (level < 0 || level >= totalLevels) {
            throw new IllegalArgumentException("Invalid level number: " + level);
        }
    }

    @Override
    public void close() {
        try {
            lock.writeLock().lock();
            levels.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }
}
