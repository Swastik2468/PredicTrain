package com.predictrack.provider;

import com.predictrack.entity.Station;
import com.predictrack.entity.WeatherData;

import java.util.Optional;

/**
 * Abstraction for fetching weather conditions for a station.
 * Can later be replaced by an external weather API implementation using station latitude/longitude.
 */
public interface WeatherProvider {

    Optional<WeatherData> findLatestWeatherForStation(Station station);
}
