package com.predictrack.service;

import com.predictrack.entity.Station;
import com.predictrack.entity.WeatherCondition;
import com.predictrack.entity.WeatherData;
import com.predictrack.provider.WeatherProvider;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service that evaluates weather conditions along railway segments and calculates
 * expected weather-related delays and human-readable explanations.
 */
@Service
public class WeatherService {

    private final WeatherProvider weatherProvider;

    public WeatherService(WeatherProvider weatherProvider) {
        this.weatherProvider = weatherProvider;
    }

    /**
     * Holds the weather delay (in minutes) and any human-readable explanation
     * for a single railway segment (fromStation -> toStation).
     */
    public record SegmentWeatherImpact(int delayMinutes, List<String> explanations) {
    }

    /**
     * Evaluates the weather impact for a single segment (fromStation -> toStation)
     * based on the latest weather forecast at the destination of the segment (toStation).
     */
    public SegmentWeatherImpact evaluateSegmentWeather(Station fromStation, Station toStation) {
        Optional<WeatherData> weatherOpt = weatherProvider.findLatestWeatherForStation(toStation);
        if (weatherOpt.isEmpty() || weatherOpt.get().getWeatherCondition() == null) {
            return new SegmentWeatherImpact(0, List.of());
        }

        WeatherCondition condition = weatherOpt.get().getWeatherCondition();
        int delay = calculateDelayForCondition(condition);

        if (delay <= 0) {
            return new SegmentWeatherImpact(0, List.of());
        }

        String explanation = buildWeatherExplanation(condition, fromStation, toStation, delay);
        List<String> explanations = new ArrayList<>();
        explanations.add(explanation);

        return new SegmentWeatherImpact(delay, explanations);
    }

    /**
     * Maps a WeatherCondition enum to an estimated segment delay in minutes.
     */
    public int calculateDelayForCondition(WeatherCondition condition) {
        if (condition == null) {
            return 0;
        }
        return switch (condition) {
            case CLEAR, CLOUDY -> 0;
            case LIGHT_RAIN -> 2;
            case MODERATE_RAIN -> 3;
            case FOG -> 6;
            case HEAVY_RAIN -> 8;
            case STORM -> 10;
        };
    }

    private String buildWeatherExplanation(
            WeatherCondition condition,
            Station fromStation,
            Station toStation,
            int delayMinutes
    ) {
        return switch (condition) {
            case HEAVY_RAIN -> "Heavy rain near " + toStation.getStationName()
                    + " may add approximately " + delayMinutes + " minutes.";
            case MODERATE_RAIN -> "Moderate rain between " + fromStation.getStationName()
                    + " and " + toStation.getStationName()
                    + " may add approximately " + delayMinutes + " minutes.";
            case LIGHT_RAIN -> "Light rain near " + toStation.getStationName()
                    + " may add approximately " + delayMinutes + " minutes.";
            case FOG -> "Fog and low visibility near " + toStation.getStationName()
                    + " may add approximately " + delayMinutes + " minutes.";
            case STORM -> "Storm conditions near " + toStation.getStationName()
                    + " may add approximately " + delayMinutes + " minutes.";
            default -> "";
        };
    }
}
