package org.example.design.patterns.strategy.pattern.correct;


import org.example.design.patterns.strategy.pattern.correct.strategy.FastEngineDriveStrategy;
import org.example.design.patterns.strategy.pattern.correct.strategy.NormalDriveStrategy;
import org.example.design.patterns.strategy.pattern.correct.vehicle.Vehicle;

public class StrategyPatternCorrect {

    public static void main(String[] args) {
        Vehicle sportsVehicle = new SportsVehicle();
        Vehicle regularVehicle = new RegularVehicle();
        Vehicle offRoadVehicle = new OffRoadVehicle();
        Vehicle passengerVehicle = new PassengerVehicle();
        Vehicle truckVehicle = new TruckVehicle();

        // Initial behavior as configured in constructors
        sportsVehicle.drive();
        regularVehicle.drive();
        offRoadVehicle.drive();
        passengerVehicle.drive();
        truckVehicle.drive();

        // Demonstrate runtime strategy change capability
        System.out.println("-- Changing strategies at runtime --");
        // Make the regular vehicle fast temporarily
        regularVehicle.setDriveStrategy(new FastEngineDriveStrategy());
        regularVehicle.drive();
        // Make the off-road vehicle normal for city driving
        offRoadVehicle.setDriveStrategy(new NormalDriveStrategy());
        offRoadVehicle.drive();
    }
}
