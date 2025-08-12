package org.example.design.patterns.strategy.pattern.wrong;

public class RegularVehicle implements Vehicle{
    @Override
    public void drive() {
        System.out.println("Normal Engine");
    }
}
