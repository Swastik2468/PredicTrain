package com.predictrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Represents weather conditions observed/forecasted at a railway station.
 * In the prototype, weather is associated with stations; later this can be
 * populated via external geographic weather APIs using station coordinates.
 */
@Entity
@Table(name = "weather_data")
public class WeatherData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private LocalDateTime forecastTime;

    @Column(nullable = false)
    private Double temperature;

    @Column(nullable = false)
    private Double rainfall;

    @Column(nullable = false)
    private Double visibility;

    @Column(nullable = false)
    private Double windSpeed;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private WeatherCondition weatherCondition;

    public WeatherData() {
    }

    public WeatherData(
            Station station,
            LocalDateTime forecastTime,
            Double temperature,
            Double rainfall,
            Double visibility,
            Double windSpeed,
            WeatherCondition weatherCondition
    ) {
        this.station = station;
        this.forecastTime = forecastTime;
        this.temperature = temperature;
        this.rainfall = rainfall;
        this.visibility = visibility;
        this.windSpeed = windSpeed;
        this.weatherCondition = weatherCondition;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public LocalDateTime getForecastTime() {
        return forecastTime;
    }

    public void setForecastTime(LocalDateTime forecastTime) {
        this.forecastTime = forecastTime;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getRainfall() {
        return rainfall;
    }

    public void setRainfall(Double rainfall) {
        this.rainfall = rainfall;
    }

    public Double getVisibility() {
        return visibility;
    }

    public void setVisibility(Double visibility) {
        this.visibility = visibility;
    }

    public Double getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(Double windSpeed) {
        this.windSpeed = windSpeed;
    }

    public WeatherCondition getWeatherCondition() {
        return weatherCondition;
    }

    public void setWeatherCondition(WeatherCondition weatherCondition) {
        this.weatherCondition = weatherCondition;
    }
}
