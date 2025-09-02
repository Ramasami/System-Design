package org.example.design.patterns.observer.pattern.stock.alert.observers;

import org.example.design.patterns.observer.pattern.stock.alert.observable.StockObservable;

public class MobileAlertObserver implements StockObserver {

    private final StockObservable stockObservable;

    public MobileAlertObserver(StockObservable stockObservable) {
        this.stockObservable = stockObservable;
        stockObservable.add(this);
    }
    @Override
    public void update() {
        System.out.println("Mobile Alert: " + stockObservable.getStock());
    }
}
