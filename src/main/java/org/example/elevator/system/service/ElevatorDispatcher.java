package org.example.elevator.system.service;

import lombok.AllArgsConstructor;
import org.example.elevator.system.model.Direction;

import java.util.List;

@AllArgsConstructor
public class ElevatorDispatcher {
    private final List<ElevatorController> elevatorControllers;
    private final ElevatorCarSelector elevatorCarSelector;


    public ElevatorController pressButton(int floor, Direction direction) {
        ElevatorController elevatorController = elevatorCarSelector.selectElevatorController(floor, direction, elevatorControllers);
        elevatorController.acceptNewRequest(floor);
        return elevatorController;
    }
}
