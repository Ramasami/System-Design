package org.example.design.patterns.strategy.pattern.correct;


import org.example.design.patterns.strategy.pattern.correct.strategy.NormalDriveStrategy;
import org.example.design.patterns.strategy.pattern.correct.vehicle.Vehicle;

public class RegularVehicle extends Vehicle {
    public RegularVehicle() {
        super(new NormalDriveStrategy());
    }
}
