package org.example.design.patterns.strategy.pattern.correct.vehicle;

import org.example.design.patterns.strategy.pattern.correct.strategy.DriveStrategy;

public abstract class Vehicle {

    private DriveStrategy driveStrategy;

    protected Vehicle(DriveStrategy driveStrategy) {
        this.driveStrategy = driveStrategy;
    }

    public void setDriveStrategy(DriveStrategy driveStrategy) {
        this.driveStrategy = driveStrategy;
    }

    public void drive() {
        this.driveStrategy.drive();
    }
}
