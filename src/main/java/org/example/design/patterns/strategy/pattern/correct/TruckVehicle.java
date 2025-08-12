package org.example.design.patterns.strategy.pattern.correct;

import org.example.design.patterns.strategy.pattern.correct.strategy.BigTorqueEngine;
import org.example.design.patterns.strategy.pattern.correct.vehicle.Vehicle;

public class TruckVehicle extends Vehicle {
    public TruckVehicle() {
        super(new BigTorqueEngine());
    }
}
