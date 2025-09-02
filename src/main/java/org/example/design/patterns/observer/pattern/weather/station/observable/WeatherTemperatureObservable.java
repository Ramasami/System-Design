package org.example.design.patterns.observer.pattern.weather.station.observable;

import org.example.design.patterns.observer.pattern.weather.station.observers.WeatherTemperatureObserver;

public interface WeatherTemperatureObservable {
    boolean add(WeatherTemperatureObserver weatherTemperatureObserver);
    boolean remove(WeatherTemperatureObserver weatherTemperatureObserver);
    void notifyObservers();
    void setData(int data);
    int getData();
}
