package org.example.elevator.system.model;

import lombok.AllArgsConstructor;
import org.example.elevator.system.service.ElevatorController;
import org.example.elevator.system.service.ElevatorDispatcher;

import java.util.List;

@AllArgsConstructor
public class ExternalButton {
    private final List<ElevatorController> elevatorController;
    private final ElevatorDispatcher elevatorDispatcher;
    public ElevatorController pressButton(int floor, Direction direction) {
        return elevatorDispatcher.pressButton(floor, direction);
    }
}
