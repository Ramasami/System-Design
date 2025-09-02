package org.example.design.patterns.observer.pattern.stock.alert;

import org.example.design.patterns.observer.pattern.stock.alert.observable.StockObservable;
import org.example.design.patterns.observer.pattern.stock.alert.observable.StockObservableImpl;
import org.example.design.patterns.observer.pattern.stock.alert.observers.EmailAlertObserver;
import org.example.design.patterns.observer.pattern.stock.alert.observers.MobileAlertObserver;
import org.example.design.patterns.observer.pattern.stock.alert.observers.StockObserver;
import org.example.design.patterns.observer.pattern.weather.station.observable.WeatherStation;
import org.example.design.patterns.observer.pattern.weather.station.observable.WeatherTemperatureObservable;
import org.example.design.patterns.observer.pattern.weather.station.observers.MobileWeatherTemperatureObserver;
import org.example.design.patterns.observer.pattern.weather.station.observers.TVWeatherTemperatureObserver;
import org.example.design.patterns.observer.pattern.weather.station.observers.WeatherTemperatureObserver;

public class ObserverApplication {
    public static void main(String[] args) {
        StockObservable stockObservable = new StockObservableImpl();

        StockObserver emailAlertObserver = new EmailAlertObserver(stockObservable);
        StockObserver mobileAlertObserver = new MobileAlertObserver(stockObservable);

        stockObservable.setStock(10);
        stockObservable.setStock(20);
        stockObservable.setStock(-30);
        stockObservable.setStock(40);
    }
}
