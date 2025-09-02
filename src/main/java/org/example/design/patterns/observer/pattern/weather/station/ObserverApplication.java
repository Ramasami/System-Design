package org.example.design.patterns.observer.pattern.weather.station;

import org.example.design.patterns.observer.pattern.weather.station.observable.WeatherTemperatureObservable;
import org.example.design.patterns.observer.pattern.weather.station.observers.WeatherTemperatureObserver;
import org.example.design.patterns.observer.pattern.weather.station.observable.WeatherStation;
import org.example.design.patterns.observer.pattern.weather.station.observers.MobileWeatherTemperatureObserver;
import org.example.design.patterns.observer.pattern.weather.station.observers.TVWeatherTemperatureObserver;

public class ObserverApplication {
    public static void main(String[] args) {
        WeatherTemperatureObservable weatherTemperatureObservable = new WeatherStation();

        WeatherTemperatureObserver mobileWeatherTemperatureObserver = new MobileWeatherTemperatureObserver(weatherTemperatureObservable);
        WeatherTemperatureObserver tvWeatherTemperatureObserver = new TVWeatherTemperatureObserver(weatherTemperatureObservable);

        weatherTemperatureObservable.setData(10);
        weatherTemperatureObservable.setData(20);
        weatherTemperatureObservable.setData(30);
    }
}
