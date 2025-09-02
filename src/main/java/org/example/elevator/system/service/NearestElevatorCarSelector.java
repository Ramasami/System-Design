package org.example.elevator.system.service;

import org.example.elevator.system.model.Direction;
import org.example.elevator.system.model.ElevatorCar;

import java.util.Comparator;
import java.util.List;

public class NearestElevatorCarSelector implements ElevatorCarSelector {
    @Override
    public ElevatorController selectElevatorController(int floor, Direction direction, List<ElevatorController> elevatorControllers) {
        return elevatorControllers.stream().map(elevatorController -> new Object[]{elevatorController, sameDirection(floor, direction, elevatorController)})
                .min(Comparator.comparingInt(a -> (int) a[1]))
                .map(x->(ElevatorController)x[0])
                .get();
    }

    private int sameDirection(int floor, Direction direction, ElevatorController elevatorController) {
        ElevatorCar elevatorCar = elevatorController.getElevatorCar();
        if (Direction.IDLE.equals(elevatorCar.getDirection())) return Math.abs(elevatorCar.getCurrentFloor() - floor);
        if (elevatorCar.getDirection().equals(direction)) {
            if (elevatorCar.getDirection().equals(Direction.UP) && elevatorCar.getCurrentFloor() <= floor) return Math.abs(elevatorCar.getCurrentFloor() - floor);
            else if (elevatorCar.getDirection().equals(Direction.DOWN) && elevatorCar.getCurrentFloor() >= floor) return Math.abs(elevatorCar.getCurrentFloor() - floor);
        } else {
            return Math.abs(elevatorCar.getCurrentFloor() - elevatorController.getSameDirection().get(elevatorController.getSameDirection().size()-1)) + Math.abs(elevatorController.getSameDirection().get(elevatorController.getSameDirection().size()-1) - floor);
        }
        return -1;
    }
}
