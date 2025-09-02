package org.example.elevator.system.model;

import lombok.AllArgsConstructor;
import org.example.elevator.system.service.ElevatorController;

@AllArgsConstructor
public class InternalButton {
    private final ElevatorController elevatorController;

    public void pressButton(int floor) {
        elevatorController.acceptNewRequest(floor);
    }
}
