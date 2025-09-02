package org.example.elevator.system.model;

import lombok.Data;
import org.example.elevator.system.service.ElevatorController;

@Data
public class ElevatorCar {
    private int id;
    private Display display;
    private int currentFloor;
    private Direction direction;
    private InternalButton internalButton;

    public ElevatorCar(int id, ElevatorController elevatorController) {
        this.id = id;
        this.display = new Display();
        this.currentFloor = 1;
        this.direction = Direction.IDLE;
        this.internalButton = new InternalButton(elevatorController);
    }
}
