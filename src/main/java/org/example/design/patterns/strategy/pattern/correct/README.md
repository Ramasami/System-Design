# Strategy Pattern — Correct Implementation

Package: `org.example.design.patterns.strategy.pattern.correct`

This package demonstrates a proper use of the Strategy design pattern, where a family of related algorithms (driving behaviors) are encapsulated and made interchangeable at runtime.

Key idea: compose a `Vehicle` with a `DriveStrategy` instead of hard‑coding behavior in each `Vehicle` subclass.

## Structure
- `DriveStrategy` — strategy interface with a single operation: `void drive()`.
- Concrete strategies:
  - `NormalDriveStrategy` — prints "Normal Engine".
  - `FastEngineDriveStrategy` — prints "Fast Engine".
  - `BigTorqueEngine` — prints "Big Torque Engine".
- `Vehicle` (abstract) — holds a `DriveStrategy` reference and delegates `drive()` to it. It also exposes `setDriveStrategy(...)` to switch behavior at runtime.
- Concrete vehicles (preconfigured with a default strategy via constructor):
  - `SportsVehicle` → `FastEngineDriveStrategy`
  - `RegularVehicle` → `NormalDriveStrategy`
  - `OffRoadVehicle` → `FastEngineDriveStrategy`
  - `PassengerVehicle` → `BigTorqueEngine`
  - `TruckVehicle` → `BigTorqueEngine`
- `StrategyPatternCorrect` — demo `main` showcasing default behavior and runtime strategy changes.

## Why this is correct
- Encapsulation of algorithms: driving behavior is isolated in strategy classes.
- Open/Closed Principle (OCP): add new driving behaviors without editing existing vehicles — just add another `DriveStrategy`.
- Composition over inheritance: vehicles are composed with a strategy rather than baking behavior into the class.
- Runtime interchangeability: you can change a vehicle’s driving behavior on the fly using `setDriveStrategy(...)`.

### Runtime strategy change capability
In the demo, we temporarily change behaviors at runtime:
```java
Vehicle regularVehicle = new RegularVehicle();
regularVehicle.drive(); // Normal Engine

regularVehicle.setDriveStrategy(new FastEngineDriveStrategy());
regularVehicle.drive(); // Fast Engine
```
This is the hallmark of the Strategy pattern in practice.

## How to run
Use your IDE to run the `main` method in:
`org.example.design.patterns.strategy.pattern.correct.StrategyPatternCorrect`

Expected console output (illustrative):
```
Fast Engine
Normal Engine
Fast Engine
Big Torque Engine
Big Torque Engine
-- Changing strategies at runtime --
Fast Engine
Normal Engine
```

## When to use
- You have multiple variants of an algorithm you want to swap seamlessly.
- You want to avoid complex conditionals and scattered behavior across subclasses.
- You need to change behavior dynamically at runtime.

## Notes
- New strategies can be added without touching any existing `Vehicle` classes.
- Different vehicles can share strategies, reducing duplication.
