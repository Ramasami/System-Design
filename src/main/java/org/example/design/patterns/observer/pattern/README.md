# Observer Pattern — org.example.design.patterns.observer.pattern

This package implements the Observer design pattern using two concrete domains:
- Weather Station: observers react to temperature updates.
- Stock Alert: observers react to inventory changes.

The examples are intentionally minimal yet idiomatic, highlighting the contracts between Subject (Observable) and Observer and showing typical update flows.

## 1. Intent
Decouple a subject that owns a piece of state from any number of observers that depend on that state, so that when the subject changes state, all observers are notified automatically.

## 2. Roles and Contracts
- Subject/Observable
  - Manages a dynamic set of observers (register/unregister).
  - Exposes a mutator that changes state and triggers notifications.
  - Notification policy: calls update() on each observer.
- Observer
  - Implements a callback update() invoked by the subject.
  - May pull the latest state from the subject during update() (pull model).

Minimal API shape used in this repo:

```
// Subject (Weather)
interface WeatherTemperatureObservable {
  boolean add(WeatherTemperatureObserver o);
  boolean remove(WeatherTemperatureObserver o);
  void notifyObservers();
  void setData(int data); // mutator
  int getData();          // state access (pull)
}

interface WeatherTemperatureObserver { void update(); }

// Subject (Stock)
interface StockObservable {
  boolean add(StockObserver o);
  boolean remove(StockObserver o);
  void notifyObservers();
  void setStock(int stock); // mutator
  int getStock();           // state access (pull)
}

interface StockObserver { void update(); }
```

## 3. Push vs Pull
- Pull model (implemented here): Subject calls update(); observers call getXxx() to retrieve state. Pros: flexible, observers choose what to read. Cons: extra calls; observers must have a reference to the subject.
- Push model: Subject calls update(newValue) passing the delta. Pros: fewer roundtrips, observers can be stateless. Cons: tighter coupling to payload shape; larger API surface.

## 4. Complexity Characteristics
- Registration/unregistration: O(1) average using HashSet.
- Notification: O(N) where N is the number of observers; each observer update is invoked exactly once per notification.
- Memory: O(N) for storing observer references.

## 5. Concurrency and Threading
These examples are single-threaded for clarity. In multithreaded scenarios consider:
- Synchronization around add/remove/notify and state access.
- Copy-on-write vs synchronized collections for observer sets.
- Reentrancy: An observer that mutates the subject during notifyObservers() can cause concurrent modification. Strategies:
  - Iterate a snapshot copy of observers.
  - Defer structural changes until after notification completes.
- Ordering: HashSet does not guarantee order; if ordering matters, prefer List or a priority structure.

## 6. Memory Management & Leaks
- Long-lived subjects with short-lived observers can leak memory if observers forget to unsubscribe. Mitigations:
  - Use weak references for observers (e.g., WeakHashMap-based registry).
  - Add explicit lifecycle hooks or auto-unsubscribe.

## 7. Error Handling Policy
- A misbehaving observer should not prevent other observers from receiving updates.
- Common strategy: wrap each observer.update() in a try/catch and log; continue to next observer. The examples keep it simple and do not include try/catch.

## 8. Variations & Related Patterns
- Event Aggregator / Pub-Sub: decouples via an event bus; observers don’t directly reference the subject.
- Reactive Streams / Rx: asynchronous, back-pressure-aware evolution of Observer.
- Property Change (JavaBeans): standardized observer for bean properties.
- Note: java.util.Observable/Observer are deprecated; prefer custom interfaces like in this repo.

## 9. Package Structure (This Repository)
- weather.station
  - observable
    - WeatherTemperatureObservable: contract for weather subject.
    - WeatherStation: maintains temperature state and a set of observers; notifies on every setData().
  - observers
    - WeatherTemperatureObserver: observer contract.
    - MobileWeatherTemperatureObserver, TVWeatherTemperatureObserver: print updates.
  - ObserverApplication: demo main() wiring station and observers.
- stock.alert
  - observable
    - StockObservable: contract for stock subject.
    - StockObservableImpl: maintains stock and notifies observers based on custom logic (see below).
  - observers
    - StockObserver: observer contract.
    - EmailAlertObserver, MobileAlertObserver: print updates.
  - ObserverApplication: demo main() wiring stock observable and observers.

## 10. Update Flow (Sequence)
1) Observer is constructed with a reference to the Subject and subscribes (subject.add(this)).
2) Client mutates the Subject’s state via setData()/setStock().
3) Subject updates internal state, then calls notifyObservers().
4) For each observer, update() is invoked; observer pulls the value via getData()/getStock() and reacts.

## 11. Notable Behavior in StockObservableImpl
- If current stock is 0, setStock(newStock) sets stock = newStock and immediately notifies observers.
- Otherwise, stock is accumulated (stock += newStock) without immediate notification.
- Rationale example: reduce notification noise while replenishing; only alert when stock transitions from zero to available. Adjust to your business rules as needed.

## 12. How to Run
Option A: From an IDE
- Run:
  - org.example.design.patterns.observer.pattern.weather.station.ObserverApplication
  - org.example.design.patterns.observer.pattern.stock.alert.ObserverApplication

Option B: From command line (Maven)
- Build: mvn -q -DskipTests package
- Weather Station: java -cp target/classes org.example.design.patterns.observer.pattern.weather.station.ObserverApplication
- Stock Alert:   java -cp target/classes org.example.design.patterns.observer.pattern.stock.alert.ObserverApplication

## 13. Extending the Examples
- Add a new observer:
  - Implement WeatherTemperatureObserver or StockObserver.
  - In the constructor, keep a reference to the subject and subject.add(this).
  - Implement update() and pull the needed state.
- Add a new subject:
  - Define an XxxObservable interface (add/remove/notify/mutator/accessor).
  - Provide a concrete implementation with a Set of observers and the state.
  - Ensure notifyObservers() is invoked under the right conditions.

## 14. FAQs
- Why HashSet for observers? O(1) average add/remove and no duplicates; ordering doesn’t matter here.
- Can I pass values directly to update()? Yes (push model), but it couples observers to the payload shape.
- How to avoid duplicate notifications? Keep idempotent observers or coalesce changes before notify.
- Is notifyObservers() synchronous? Yes in this repo; for async, queue tasks or use an event bus/reactive library.

## 15. Key Takeaways
- Observer enables runtime composition and loose coupling.
- Define clear contracts and notification policies.
- Consider concurrency, error isolation, and lifecycle to avoid pitfalls in production systems.
