# Decorator Pattern — org.example.design.patterns.decorator.pattern

This package implements the Decorator design pattern with a pizza/toppings domain:
- Base Pizzas: Farmhouse, Margherita, PeppyPaneer
- Toppings (Decorators): ExtraCheese, ExtraOnions, ExtraOlives

The examples focus on runtime composition of features (toppings) without exploding the number of subclasses.

## 1. Intent
Attach additional responsibilities to an object dynamically. Decorators provide a flexible alternative to subclassing for extending functionality.

## 2. Roles and Contracts
- Component
  - The common interface shared by both concrete components and decorators.
  - In this repo: `BasePizza` with `int getCost()`.
- ConcreteComponent
  - A default implementation of the Component to which responsibilities can be added.
  - Here: `FarmhouseBasePizza`, `MargheritaBasePizza`, `PeppyPaneerBasePizza`.
- Decorator (a.k.a. Wrapper)
  - Maintains a reference to a Component and implements the Component interface by delegating and then augmenting behavior.
  - Here: abstract `Toppings` implements `BasePizza` and holds a `BasePizza pizza;`.
- ConcreteDecorator
  - Adds responsibilities by overriding operations and invoking the wrapped component first.
  - Here: `ExtraCheese`, `ExtraOnions`, `ExtraOlives`.

Minimal API shape used in this repo:

```
// Component
public interface BasePizza { int getCost(); }

// Concrete components
public class FarmhouseBasePizza implements BasePizza { public int getCost() { return 100; } }
public class MargheritaBasePizza implements BasePizza { public int getCost() { return 120; } }
public class PeppyPaneerBasePizza implements BasePizza { public int getCost() { return 110; } }

// Decorator base
public abstract class Toppings implements BasePizza {
  protected final BasePizza pizza;
  protected Toppings(BasePizza pizza) { this.pizza = pizza; }
}

// Concrete decorators
public class ExtraCheese extends Toppings {
  public ExtraCheese(BasePizza pizza) { super(pizza); }
  public int getCost() { return pizza.getCost() + 20; }
}
public class ExtraOnions extends Toppings {
  public ExtraOnions(BasePizza pizza) { super(pizza); }
  public int getCost() { return pizza.getCost() + 10; }
}
public class ExtraOlives extends Toppings {
  public ExtraOlives(BasePizza pizza) { super(pizza); }
  public int getCost() { return pizza.getCost() + 40; }
}
```

## 3. Composition at Runtime
You can arbitrarily layer decorators to build complex behavior at runtime without new subclasses:

```
BasePizza base = new FarmhouseBasePizza();           // 100
BasePizza withCheese = new ExtraCheese(base);        // +20 -> 120
BasePizza withOnions = new ExtraOnions(withCheese);  // +10 -> 130
BasePizza withOlives = new ExtraOlives(withOnions);  // +40 -> 170
int cost = withOlives.getCost(); // 170
```

Order matters only if your decorators implement non-commutative behavior. In this example all costs are additive and independent, so order doesn’t affect the final price, but that’s an implementation detail you control in each decorator.

## 4. Example: DecoratorApplication
Main entry point: `org.example.design.patterns.decorator.pattern.DecoratorApplication`

```
public class DecoratorApplication {
  public static void main(String[] args) {
    BasePizza farmhouseBasePizza = new FarmhouseBasePizza(); // 100
    BasePizza pizzaWithToppings = new ExtraCheese(
        new ExtraOlives(
            new ExtraOnions(
                new ExtraCheese(farmhouseBasePizza))));

    System.out.println(pizzaWithToppings.getCost()); // 100 + 20 + 10 + 40 + 20 = 190
  }
}
```

## 5. Characteristics and Complexity
- Time complexity for a single operation (e.g., getCost): O(k) where k is the number of decorators in the chain, since each decorator delegates to the next.
- Memory: O(k) references for the decorator chain per composed object.
- Instantiation: Flexible; adding a new variant is just composing existing decorators rather than creating a brand-new subclass per combination.

## 6. When to Use Decorator
- You need to add responsibilities to objects dynamically and transparently, without affecting other objects.
- Subclassing would create an explosion of classes for all feature combinations.
- You want to stick to the Open/Closed Principle: open for extension, closed for modification.

## 7. Immutability and Side Effects
- In this example decorators are stateless wrappers around `getCost()`; they do not mutate wrapped objects.
- If your decorators cache or maintain state, be explicit about thread-safety and visibility.

## 8. Extending the Example
- Add a new base pizza:
  - Implement `BasePizza` and return a base cost in `getCost()`.
- Add a new topping (decorator):
  - Extend `Toppings` and override `getCost()` by delegating to `pizza.getCost()` and then applying the additional behavior (e.g., add a surcharge).
- Add behavior beyond cost:
  - You can expand `BasePizza` to include methods like `getDescription()` and have decorators concatenate descriptions.

## 9. How to Run
Option A: From an IDE
- Run: `org.example.design.patterns.decorator.pattern.DecoratorApplication`

Option B: From command line (Maven)
- Build: `mvn -q -DskipTests package`
- Run:  `java -cp target/classes org.example.design.patterns.decorator.pattern.DecoratorApplication`

Expected output for the provided main: `190`

## 10. FAQs
- Why not just use inheritance for toppings? Because you’d need a subclass for every combination (e.g., Margherita+Cheese+Olives+Onions), which scales poorly. Decorator composes behavior.
- Do decorators have to add cost? No; they can modify or transform any operation exposed by the component (e.g., apply discounts, taxes, or logging).
- Can I unwrap a decorator? Generally you program to the `BasePizza` interface; if you need to inspect layers, you can expose the wrapped component from `Toppings`, but this couples clients to the decorator details.

## 11. Key Takeaways
- Decorator enables flexible, runtime composition.
- Program to interfaces and delegate, then augment.
- Prefer small, focused decorators that are easy to combine and test.
