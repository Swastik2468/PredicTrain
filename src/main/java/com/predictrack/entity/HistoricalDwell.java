package com.predictrack.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Raw historical station dwell (halt) time record for a train at a station
 * on a specific past journey date.
 * Used to calculate the median expected dwell time for (train + station).
 */
@Entity
@Table(name = "historical_dwells")
public class HistoricalDwell {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @Column(nullable = false)
    private LocalDate journeyDate;

    @Column(nullable = false)
    private LocalTime actualArrival;

    @Column(nullable = false)
    private LocalTime actualDeparture;

    @Column(nullable = false)
    private Integer dwellMinutes;

    public HistoricalDwell() {
    }

    public HistoricalDwell(
            Train train,
            Station station,
            LocalDate journeyDate,
            LocalTime actualArrival,
            LocalTime actualDeparture,
            Integer dwellMinutes
    ) {
        this.train = train;
        this.station = station;
        this.journeyDate = journeyDate;
        this.actualArrival = actualArrival;
        this.actualDeparture = actualDeparture;
        this.dwellMinutes = dwellMinutes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }

    public Station getStation() {
        return station;
    }

    public void setStation(Station station) {
        this.station = station;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public LocalTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(LocalTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    public LocalTime getActualDeparture() {
        return actualDeparture;
    }

    public void setActualDeparture(LocalTime actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    public Integer getDwellMinutes() {
        return dwellMinutes;
    }

    public void setDwellMinutes(Integer dwellMinutes) {
        this.dwellMinutes = dwellMinutes;
    }
}
