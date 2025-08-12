package org.example.design.patterns.strategy.pattern.wrong;

public class OffRoadVehicle implements  Vehicle {
    @Override
    public void drive() {
        System.out.println("Fast Engine");
    }
}
