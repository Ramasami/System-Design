package org.example.design.patterns.strategy.pattern.correct;


import org.example.design.patterns.strategy.pattern.correct.strategy.FastEngineDriveStrategy;
import org.example.design.patterns.strategy.pattern.correct.vehicle.Vehicle;

public class OffRoadVehicle extends Vehicle {

    public OffRoadVehicle() {
        super(new FastEngineDriveStrategy());
    }
}
