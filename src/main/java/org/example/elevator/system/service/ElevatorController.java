package org.example.elevator.system.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.elevator.system.model.Direction;
import org.example.elevator.system.model.ElevatorCar;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
public class ElevatorController {
    private List<Integer> sameDirection;
    private ElevatorCar elevatorCar;

    public ElevatorController() {
        sameDirection = new ArrayList<>();
    }

    public void acceptNewRequest(int floor) {
        if (sameDirection.contains(floor)) return;
        if (sameDirection.isEmpty()) {
          if (elevatorCar.getCurrentFloor() < floor) elevatorCar.setDirection(Direction.UP);
          else  elevatorCar.setDirection(Direction.DOWN);
          sameDirection.add(floor);
        } else if (sameDirection.get(0) > floor) {
            if (elevatorCar.getDirection() == Direction.UP) {
                sameDirection.add(floor);
            } else {
                int i;
                for (i = 0; i < sameDirection.size(); i++) {
                    if (sameDirection.get(i) < floor) {
                        sameDirection.add(i, floor);
                        return;
                    }
                }
                sameDirection.add(floor);
            }
        } else {
            if (elevatorCar.getDirection() == Direction.DOWN) {
                sameDirection.add(floor);
            } else {
                int i;
                for (i = 0; i < sameDirection.size(); i++) {
                    if (sameDirection.get(i) > floor) {
                        sameDirection.add(i, floor);
                        return;
                    }
                }
                sameDirection.add(floor);
            }
        }
    }

    public void controlCar() {
        if (sameDirection.isEmpty()) {
            elevatorCar.setDirection(Direction.IDLE);
        } else if (elevatorCar.getDirection() == Direction.IDLE) {
            elevatorCar.setDirection(sameDirection.get(0) > elevatorCar.getCurrentFloor() ? Direction.UP : Direction.DOWN);
            elevatorCar.setCurrentFloor(elevatorCar.getDirection() == Direction.UP ? elevatorCar.getCurrentFloor() + 1 : elevatorCar.getCurrentFloor() - 1);
            checkReached();
        } else {
            elevatorCar.setDirection(sameDirection.get(0) > elevatorCar.getCurrentFloor() ? Direction.UP : Direction.DOWN);
            elevatorCar.setCurrentFloor(elevatorCar.getDirection() == Direction.UP ? elevatorCar.getCurrentFloor() + 1 : elevatorCar.getCurrentFloor() - 1);
            checkReached();
        }
    }

    public void checkReached() {
        if (sameDirection.isEmpty()) return;
        if (elevatorCar.getCurrentFloor() == sameDirection.get(0)) {
            sameDirection.remove(0);
            if (sameDirection.isEmpty()) elevatorCar.setDirection(Direction.IDLE);
            else if (sameDirection.get(0) > elevatorCar.getCurrentFloor()) {
                elevatorCar.setDirection(Direction.UP);
            } else {
                elevatorCar.setDirection(Direction.DOWN);
            }
        }
    }
}
