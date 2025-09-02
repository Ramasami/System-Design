package org.example.elevator.system.service;

import org.example.elevator.system.model.Direction;

import java.util.List;

public interface ElevatorCarSelector {
    ElevatorController selectElevatorController(int floor, Direction direction, List<ElevatorController> elevatorControllers);
}
