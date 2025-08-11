package org.example.parking.lot;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Getter
@AllArgsConstructor
public class Level {
    private static final Logger logger = Logger.getLogger(Level.class.getName());
    private final int levelNumber;
    private final Map<VehicleType, Set<ParkingSpot>> reservedParkingSpots;
    private final Map<VehicleType, List<ParkingSpot>> freeParkingSpots;
    private final AtomicInteger spotCounter;

    public Level(int levelNumber) {
        if (levelNumber < 0) {
            throw new IllegalArgumentException("Level number cannot be negative");
        }
        this.levelNumber = levelNumber;
        this.reservedParkingSpots = new ConcurrentHashMap<>();
        this.freeParkingSpots = new ConcurrentHashMap<>();
        this.spotCounter = new AtomicInteger(0);
    }

    public int getFreeParkingSpotsCount(VehicleType vehicleType) {
        return freeParkingSpots.getOrDefault(vehicleType, Collections.emptyList()).size();
    }

    public boolean addParkingSpot(int level, VehicleType vehicleType) {
        return freeParkingSpots.computeIfAbsent(vehicleType, k ->
                Collections.synchronizedList(new ArrayList<>())).add(new ParkingSpot(vehicleType, spotCounter.getAndIncrement(), level));
    }

    public Optional<ParkingSpot> reserveParkingSpot(VehicleType vehicleType, Vehicle vehicle) {
        List<ParkingSpot> spots = freeParkingSpots.get(vehicleType);
        if (spots == null || spots.isEmpty()) {
            return Optional.empty();
        }
        synchronized (spots) {
            if (spots.isEmpty()) {
                return Optional.empty();
            }
            ParkingSpot spot = spots.remove(0);
            if (spot.reserve(vehicle)) {
                reservedParkingSpots.computeIfAbsent(vehicleType, k ->
                        Collections.newSetFromMap(new ConcurrentHashMap<>())).add(spot);
                return Optional.of(spot);
            }
            spots.add(spot);
            return Optional.empty();
        }
    }

    public boolean unReserveParkingSpot(ParkingSpot spot) {
        VehicleType vehicleType = spot.getVehicleType();
        if (!spot.unReserve()) {
            return false;
        }
        Set<ParkingSpot> reserved = reservedParkingSpots.get(vehicleType);
        if (reserved != null) {
            synchronized (reserved) {
                reserved.remove(spot);
                if (reserved.isEmpty()) {
                    reservedParkingSpots.remove(vehicleType);
                }
            }
        }
        freeParkingSpots.computeIfAbsent(vehicleType, k ->
                Collections.synchronizedList(new ArrayList<>())).add(spot);
        return true;
    }
}
