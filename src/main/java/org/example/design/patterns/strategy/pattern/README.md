# Strategy Pattern — A Practical Guide (with Java examples)

The Strategy pattern is a behavioral design pattern that lets you define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.

Core idea: Prefer composition over inheritance. Instead of hard-coding behavior into a class (or into subclasses), inject a strategy object that implements a shared interface.

---

## When should you use Strategy?
- You have several variants of an algorithm (e.g., payment methods, compression formats, sorting strategies).
- You want to switch behavior at runtime based on configuration, user input, or environment.
- You want to eliminate conditionals (if/else or switch) scattered across the code.
- You want to adhere to the Open/Closed Principle: add new algorithms without modifying existing clients.

When Strategy may not be ideal:
- The set of algorithms almost never changes and runtime swapping isn’t needed.
- The overhead of additional classes is not justified for a very small/simple codebase.

---

## Structure
- Strategy interface: declares the operation common to all algorithms.
- Concrete strategies: implement the algorithm in different ways.
- Context: holds a reference to a strategy and delegates the work to it. It may allow changing the strategy at runtime.

UML sketch (textual):
- Context — has-a —> Strategy
- Strategy — implemented by —> ConcreteStrategyA, ConcreteStrategyB, ...

---

## Minimal Java example

Strategy interface:
```java
public interface CompressionStrategy {
    byte[] compress(byte[] input);
}
```

Concrete strategies:
```java
public class ZipCompressionStrategy implements CompressionStrategy {
    @Override public byte[] compress(byte[] input) {
        System.out.println("Compressing with ZIP");
        return input; // stub
    }
}

public class GzipCompressionStrategy implements CompressionStrategy {
    @Override public byte[] compress(byte[] input) {
        System.out.println("Compressing with GZIP");
        return input; // stub
    }
}
```

Context:
```java
public class Compressor {
    private CompressionStrategy strategy;

    public Compressor(CompressionStrategy strategy) {
        this.strategy = strategy;
    }

    public void setStrategy(CompressionStrategy strategy) {
        this.strategy = strategy; // runtime swapping
    }

    public byte[] compress(byte[] data) {
        return strategy.compress(data);
    }
}
```

Usage:
```java
Compressor compressor = new Compressor(new ZipCompressionStrategy());
compressor.compress("hello".getBytes());

// Switch at runtime
compressor.setStrategy(new GzipCompressionStrategy());
compressor.compress("world".getBytes());
```

---

## Real example from this repository

This repo includes a Strategy-based "Vehicle driving" example under:
- Correct: `org.example.design.patterns.strategy.pattern.correct`
- Incorrect (anti-pattern): `org.example.design.patterns.strategy.pattern.wrong`

Key classes (correct):
- Strategy interface: `DriveStrategy`
- Concrete strategies: `FastEngineDriveStrategy`, `NormalDriveStrategy`, `BigTorqueEngine`
- Context: abstract `Vehicle` composing a `DriveStrategy` and delegating `drive()` to it
- Concrete contexts: `SportsVehicle`, `RegularVehicle`, `OffRoadVehicle`, `PassengerVehicle`, `TruckVehicle` (each preconfigures a default strategy)
- Demo: `StrategyPatternCorrect` (shows runtime swapping via `setDriveStrategy(...)`)

Why the wrong example is wrong: vehicles hard-code behavior inside `drive()`, with no separate strategy interface or runtime interchangeability.

---

## Benefits
- Open/Closed Principle: add new strategies without changing existing clients.
- Single Responsibility: algorithm variations live in their own classes.
- Runtime flexibility: swap strategies dynamically.
- Testability: strategies can be unit tested in isolation; contexts can be tested with mocks.

## Trade-offs
- More classes and indirection.
- The context must expose a seam (constructor/setter) for strategy injection.

---

## Common pitfalls and how to avoid them
- Treating subclasses as strategies: If behavior is hard-coded in subclasses, you cannot change it at runtime.
  - Fix: extract a strategy interface and compose it in the context class.
- Leaking strategy-specific APIs: Keep strategy interfaces minimal and cohesive.
- Overusing Strategy for trivial differences: Evaluate cost/benefit.

---

## Strategy vs. similar patterns
- State: Both use composition and delegation, but State changes behavior based on internal state transitions; Strategy is chosen by clients (often explicitly) for interchangeable algorithms.
- Template Method: Behavior variation via inheritance and hooks; Strategy uses composition, enabling runtime swapping.
- Decorator: Adds responsibilities without changing the algorithm contract; Strategy swaps the algorithm itself.

---

## Testing tips
- Unit-test each strategy class independently with representative inputs.
- Test the context with a test-double strategy to verify delegation.
- Verify runtime swapping works as expected (e.g., change strategy and assert different outcome/output).

---

## Quick link to examples here
- Correct implementation: `src/main/java/org/example/design/patterns/strategy/pattern/correct/`
- Anti-pattern (what not to do): `src/main/java/org/example/design/patterns/strategy/pattern/wrong/`

Run the demos by executing the `main` methods in `StrategyPatternCorrect` and `StrategyPatternWrong` from your IDE.