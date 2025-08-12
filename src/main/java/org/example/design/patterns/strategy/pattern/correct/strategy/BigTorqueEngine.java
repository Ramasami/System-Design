package org.example.design.patterns.strategy.pattern.correct.strategy;

public class BigTorqueEngine implements  DriveStrategy {
    @Override
    public void drive() {
        System.out.println("Big Torque Engine");
    }
}
