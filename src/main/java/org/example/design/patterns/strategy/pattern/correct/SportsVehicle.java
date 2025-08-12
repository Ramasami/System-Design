package org.example.design.patterns.strategy.pattern.correct;

import org.example.design.patterns.strategy.pattern.correct.strategy.FastEngineDriveStrategy;
import org.example.design.patterns.strategy.pattern.correct.vehicle.Vehicle;

public class SportsVehicle extends Vehicle {
    public SportsVehicle() {
        super(new FastEngineDriveStrategy());
    }
}
