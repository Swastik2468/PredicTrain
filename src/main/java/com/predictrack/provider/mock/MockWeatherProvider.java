package com.predictrack.provider.mock;

import com.predictrack.entity.Station;
import com.predictrack.entity.WeatherData;
import com.predictrack.provider.WeatherProvider;
import com.predictrack.repository.WeatherDataRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mock implementation of WeatherProvider backed by our MySQL database.
 */
@Component
public class MockWeatherProvider implements WeatherProvider {

    private final WeatherDataRepository weatherDataRepository;

    public MockWeatherProvider(WeatherDataRepository weatherDataRepository) {
        this.weatherDataRepository = weatherDataRepository;
    }

    @Override
    public Optional<WeatherData> findLatestWeatherForStation(Station station) {
        return weatherDataRepository.findFirstByStationOrderByForecastTimeDesc(station);
    }
}
