package org.example.design.patterns.observer.pattern.stock.alert.observable;

import org.example.design.patterns.observer.pattern.stock.alert.observers.StockObserver;

public interface StockObservable {
    boolean add(StockObserver stockObserver);
    boolean remove(StockObserver stockObserver);
    void notifyObservers();
    void setStock(int stock);
    int getStock();
}
