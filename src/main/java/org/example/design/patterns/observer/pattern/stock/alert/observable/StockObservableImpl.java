package org.example.design.patterns.observer.pattern.stock.alert.observable;

import org.example.design.patterns.observer.pattern.stock.alert.observers.StockObserver;

import java.util.HashSet;
import java.util.Set;

public class StockObservableImpl implements StockObservable {

    private static final Set<StockObserver> WEATHER_TEMPERATURE_OBSERVERS = new HashSet<>();
    private int stock;

    @Override
    public boolean add(StockObserver stockObserver) {
        return WEATHER_TEMPERATURE_OBSERVERS.add(stockObserver);
    }

    @Override
    public boolean remove(StockObserver stockObserver) {
        return WEATHER_TEMPERATURE_OBSERVERS.remove(stockObserver);
    }

    @Override
    public void notifyObservers() {
        WEATHER_TEMPERATURE_OBSERVERS.forEach(StockObserver::update);
    }

    @Override
    public void setStock(int newStock) {
        if (this.stock == 0) {
            stock = newStock;
            notifyObservers();
        } else {
            stock += newStock;
        }
    }

    public int getStock() {
        return stock;
    }
}
