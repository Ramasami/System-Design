package org.example.design.patterns.observer.pattern.weather.station.observers;

import org.example.design.patterns.observer.pattern.weather.station.observable.WeatherTemperatureObservable;

public class TVWeatherTemperatureObserver implements WeatherTemperatureObserver {

    private final WeatherTemperatureObservable weatherTemperatureObservable;

    public TVWeatherTemperatureObserver(WeatherTemperatureObservable weatherTemperatureObservable) {
        this.weatherTemperatureObservable = weatherTemperatureObservable;
        weatherTemperatureObservable.add(this);
    }
    @Override
    public void update() {
        System.out.println("TV Observer data: " + weatherTemperatureObservable.getData());
    }
}
