package org.example.design.patterns.observer.pattern.weather.station.observable;

import org.example.design.patterns.observer.pattern.weather.station.observers.WeatherTemperatureObserver;

import java.util.HashSet;
import java.util.Set;

public class WeatherStation implements WeatherTemperatureObservable {

    private static final Set<WeatherTemperatureObserver> WEATHER_TEMPERATURE_OBSERVERS = new HashSet<>();
    private int data;

    @Override
    public boolean add(WeatherTemperatureObserver weatherTemperatureObserver) {
        return WEATHER_TEMPERATURE_OBSERVERS.add(weatherTemperatureObserver);
    }

    @Override
    public boolean remove(WeatherTemperatureObserver weatherTemperatureObserver) {
        return WEATHER_TEMPERATURE_OBSERVERS.remove(weatherTemperatureObserver);
    }

    @Override
    public void notifyObservers() {
        WEATHER_TEMPERATURE_OBSERVERS.forEach(WeatherTemperatureObserver::update);
    }

    @Override
    public void setData(int data) {
        this.data = data;
        notifyObservers();
    }

    public int getData() {
        return  data;
    }
}
