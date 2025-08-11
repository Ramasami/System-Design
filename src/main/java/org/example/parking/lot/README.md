# Parking Lot System

A thread-safe, multi-level parking lot management system implemented in Java. The system supports different vehicle types and provides concurrent parking operations.

## Features

- Multi-level parking structure
- Support for different vehicle types (Car, Bike, Truck)
- Thread-safe operations with read-write locks
- Dynamic parking spot allocation
- Concurrent parking and unparking operations

## Class Structure

- `ParkingLotSystem`: Main system controller managing multiple levels
- `Level`: Manages parking spots for a single level
- `ParkingSpot`: Individual parking spot with vehicle type and status
- `Vehicle`: Represents a vehicle with type and identification
- `VehicleType`: Enum of supported vehicle types

## Usage Example

```java
// Initialize parking lot with 3 levels
ParkingLotSystem parkingLot = new ParkingLotSystem(3);

// Add parking spots to different levels
parkingLot.addParkingSpot(0, VehicleType.CAR);
parkingLot.addParkingSpot(1, VehicleType.BIKE);

// Create and park vehicles
Vehicle car = new Vehicle(VehicleType.CAR, 1);
parkingLot.parkVehicle(car);

// Unpark vehicles
parkingLot.unParkVehicle(car);
```

## Thread Safety

The system implements thread-safe operations using:
- ReadWriteLocks for concurrent access
- ConcurrentHashMap for vehicle tracking
- Synchronized collections for spot management
- Atomic operations for spot numbering

## Requirements

- Java 8 or higher
- Maven for dependency management
- Lombok library

## Dependencies

- Lombok: For reducing boilerplate code
- Java Logging: For system logging

## Best Practices

- Always close the ParkingLotSystem when done
- Handle Optional returns for parking operations
- Check return values for successful operations
- Use proper exception handling

## License

This project is available under the MIT License.