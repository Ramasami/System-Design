package org.example.design.patterns.strategy.pattern.wrong;

public class StrategyPatternWrong {

    public static void main(String[] args) {
        Vehicle sportsVehicle = new SportsVehicle();
        Vehicle regularVehicle = new RegularVehicle();
        Vehicle offRoadVehicle = new OffRoadVehicle();
        Vehicle passengerVehicle = new PassengerVehicle();
        Vehicle truckVehicle = new TruckVehicle();

        sportsVehicle.drive();
        regularVehicle.drive();
        offRoadVehicle.drive();
        passengerVehicle.drive();
        truckVehicle.drive();
    }
}
