# Strategy Pattern — Incorrect (Anti‑Pattern) Example

Package: `org.example.design.patterns.strategy.pattern.wrong`

This package shows a common misunderstanding: pushing behavior directly into each `Vehicle` implementation and calling it a "strategy". While it compiles and runs, it is not the Strategy pattern.

## What’s implemented here
- `Vehicle` is an interface with a single `drive()` method.
- Each concrete vehicle (`SportsVehicle`, `RegularVehicle`, `OffRoadVehicle`, `PassengerVehicle`, `TruckVehicle`) hard‑codes its own driving behavior directly inside `drive()`.
- `StrategyPatternWrong` runs a simple demo.

## Why this is not Strategy
- No strategy interface for the algorithm family: There is no separate `DriveStrategy` abstraction.
- Behavior is not encapsulated: The driving algorithm is embedded in each vehicle class.
- No runtime interchangeability: You cannot change a vehicle’s driving behavior at runtime without replacing the object with another class.
- Tight coupling and duplication: Multiple classes print similar messages; changes require modifying many classes.
- Violates Open/Closed Principle (OCP): To add a new driving behavior, you must change (or create) vehicle classes rather than adding a new strategy type.

In short, this is inheritance-based specialization with hard‑coded behavior, not a Strategy pattern.

## Contrast with the correct approach
See `org.example.design.patterns.strategy.pattern.correct`:
- Behavior is represented by a `DriveStrategy` interface with swappable concrete strategies.
- `Vehicle` composes a strategy and delegates to it.
- Behavior can be changed at runtime via `setDriveStrategy(...)` without creating a new subclass.

## How to run
Use your IDE to run the `main` method in:
`org.example.design.patterns.strategy.pattern.wrong.StrategyPatternWrong`

Expected console output (illustrative):
```
Fast Engine
Normal Engine
Fast Engine
Big Torque Engine
Big Torque Engine
```

## Takeaways
- If you need to swap behaviors or add new ones frequently, prefer the Strategy pattern from the `correct` package.
- Hard‑coding behavior in subclasses leads to duplication and rigidity.
