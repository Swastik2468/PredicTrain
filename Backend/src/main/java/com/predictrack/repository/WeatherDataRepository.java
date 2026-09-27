package com.predictrack.repository;

import com.predictrack.entity.Station;
import com.predictrack.entity.WeatherData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WeatherDataRepository extends JpaRepository<WeatherData, Long> {

    Optional<WeatherData> findFirstByStationOrderByForecastTimeDesc(Station station);

    Optional<WeatherData> findFirstByStation_StationCodeOrderByForecastTimeDesc(String stationCode);

    List<WeatherData> findByStation(Station station);
}
