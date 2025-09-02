package org.example.elevator.system;

import org.example.elevator.system.model.*;
import org.example.elevator.system.service.ElevatorController;
import org.example.elevator.system.service.ElevatorDispatcher;
import org.example.elevator.system.service.NearestElevatorCarSelector;

import java.util.ArrayList;
import java.util.List;

public class ElevatorApplication {

    public static void main(String[] args) {
        ElevatorController controller1 = new ElevatorController();
        ElevatorCar elevatorCar1 = new ElevatorCar(1, controller1);
        controller1.setElevatorCar(elevatorCar1);

        ElevatorController controller2 = new ElevatorController();
        ElevatorCar elevatorCar2 = new ElevatorCar(1, controller2);
        controller2.setElevatorCar(elevatorCar2);

        List<ElevatorController> elevatorControllers = new ArrayList<>();
        elevatorControllers.add(controller1);
        elevatorControllers.add(controller2);

        ExternalButton externalButton = new ExternalButton(elevatorControllers, new ElevatorDispatcher(elevatorControllers, new NearestElevatorCarSelector()));

        printElevator(elevatorControllers);

        externalButton.pressButton(3, Direction.UP).getElevatorCar().getInternalButton().pressButton(5);
        printElevator(elevatorControllers);
        externalButton.pressButton(4, Direction.UP);
        externalButton.pressButton(2, Direction.DOWN).getElevatorCar().getInternalButton().pressButton(0);

        printElevator(elevatorControllers);
        externalButton.pressButton(2, Direction.UP);

        printElevator(elevatorControllers);
        printElevator(elevatorControllers);
        printElevator(elevatorControllers);
        printElevator(elevatorControllers);
        printElevator(elevatorControllers);
        printElevator(elevatorControllers);
    }

    private static void printElevator(List<ElevatorController> elevatorControllers) {
        for (ElevatorController elevatorController : elevatorControllers) {
            System.out.println("Current Floor: " + elevatorController.getElevatorCar().getCurrentFloor() + " Direction: " + elevatorController.getElevatorCar().getDirection() + " " + elevatorController.getSameDirection());
            elevatorController.controlCar();
        }
        System.out.println();
    }
}
