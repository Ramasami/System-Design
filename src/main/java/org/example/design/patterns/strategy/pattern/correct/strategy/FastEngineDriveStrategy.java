package org.example.design.patterns.strategy.pattern.correct.strategy;

public class FastEngineDriveStrategy implements DriveStrategy {
    @Override
    public void drive() {
        System.out.println("Fast Engine");
    }
}
