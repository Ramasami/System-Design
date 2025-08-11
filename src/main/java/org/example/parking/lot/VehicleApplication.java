package org.example.parking.lot;

public class VehicleApplication {

    public static void main(String[] args) {
        ParkingLotSystem parkingLot = new ParkingLotSystem(3);
        parkingLot.addParkingSpot(0, VehicleType.CAR);
        parkingLot.addParkingSpot(2, VehicleType.CAR);
        parkingLot.addParkingSpot(1, VehicleType.BIKE);
        parkingLot.addParkingSpot(1, VehicleType.BIKE);
        parkingLot.addParkingSpot(1, VehicleType.TRUCK);

        Vehicle car1 = new Vehicle(VehicleType.CAR, 1);
        Vehicle car2 = new Vehicle(VehicleType.CAR, 2);
        Vehicle bike1 = new Vehicle(VehicleType.BIKE, 3);
        Vehicle bike2 = new Vehicle(VehicleType.BIKE, 4);
        Vehicle truck1 = new Vehicle(VehicleType.TRUCK, 5);
        Vehicle truck2 = new Vehicle(VehicleType.TRUCK, 6);

        parkingLot.parkVehicle(car1);
        parkingLot.parkVehicle(car2);
        parkingLot.parkVehicle(bike1);
        parkingLot.parkVehicle(bike2);
        parkingLot.parkVehicle(truck1);
        parkingLot.parkVehicle(truck2);

        parkingLot.unParkVehicle(car1);
        parkingLot.unParkVehicle(bike1);
    }
}
