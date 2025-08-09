package org.example.parking.lot;

import java.util.ArrayList;
import java.util.List;

public class ParkingLot {

    private static ParkingLot parkingLot;
    private final List<Level> levels;

    private ParkingLot() {
        this.levels = new ArrayList<>();
    }


    public static ParkingLot getInstance() {
        if (parkingLot == null) {
            synchronized (ParkingLot.class) {
                if (parkingLot == null) {
                    parkingLot = new ParkingLot();
                }
            }
        }
        return parkingLot;
    }

    public void addLevel(Level level) {
        this.levels.add(level);
    }


}
