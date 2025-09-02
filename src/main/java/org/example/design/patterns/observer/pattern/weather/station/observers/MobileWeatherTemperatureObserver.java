package org.example.design.patterns.observer.pattern.weather.station.observers;

import org.example.design.patterns.observer.pattern.weather.station.observable.WeatherTemperatureObservable;

public class MobileWeatherTemperatureObserver implements WeatherTemperatureObserver {

    private final WeatherTemperatureObservable weatherTemperatureObservable;

    public MobileWeatherTemperatureObserver(WeatherTemperatureObservable weatherTemperatureObservable) {
        this.weatherTemperatureObservable = weatherTemperatureObservable;
        weatherTemperatureObservable.add(this);
    }
    @Override
    public void update() {
        System.out.println("Mobile Observer data: " + weatherTemperatureObservable.getData());
    }
}
