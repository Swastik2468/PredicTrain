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
 * Raw historical running-time record for a train between two consecutive stations
 * on a specific past journey date.
 * Used to calculate the median baseline running time for (train + fromStation + toStation).
 */
@Entity
@Table(name = "historical_segment_times")
public class HistoricalSegmentTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "from_station_id", nullable = false)
    private Station fromStation;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "to_station_id", nullable = false)
    private Station toStation;

    @Column(nullable = false)
    private LocalDate journeyDate;

    @Column(nullable = false)
    private LocalTime actualDeparture;

    @Column(nullable = false)
    private LocalTime actualArrival;

    @Column(nullable = false)
    private Integer runningTimeMinutes;

    public HistoricalSegmentTime() {
    }

    public HistoricalSegmentTime(
            Train train,
            Station fromStation,
            Station toStation,
            LocalDate journeyDate,
            LocalTime actualDeparture,
            LocalTime actualArrival,
            Integer runningTimeMinutes
    ) {
        this.train = train;
        this.fromStation = fromStation;
        this.toStation = toStation;
        this.journeyDate = journeyDate;
        this.actualDeparture = actualDeparture;
        this.actualArrival = actualArrival;
        this.runningTimeMinutes = runningTimeMinutes;
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

    public Station getFromStation() {
        return fromStation;
    }

    public void setFromStation(Station fromStation) {
        this.fromStation = fromStation;
    }

    public Station getToStation() {
        return toStation;
    }

    public void setToStation(Station toStation) {
        this.toStation = toStation;
    }

    public LocalDate getJourneyDate() {
        return journeyDate;
    }

    public void setJourneyDate(LocalDate journeyDate) {
        this.journeyDate = journeyDate;
    }

    public LocalTime getActualDeparture() {
        return actualDeparture;
    }

    public void setActualDeparture(LocalTime actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    public LocalTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(LocalTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    public Integer getRunningTimeMinutes() {
        return runningTimeMinutes;
    }

    public void setRunningTimeMinutes(Integer runningTimeMinutes) {
        this.runningTimeMinutes = runningTimeMinutes;
    }
}
