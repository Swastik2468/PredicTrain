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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalTime;

/**
 * Defines one station stop in a train's ordered route.
 * The sequenceNumber (1, 2, 3, ...) determines the exact station ordering along the route.
 */
@Entity
@Table(
    name = "train_routes",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"train_id", "sequenceNumber"}),
        @UniqueConstraint(columnNames = {"train_id", "station_id"})
    }
)
public class TrainRoute {

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
    private Integer sequenceNumber;

    @Column(nullable = false)
    private LocalTime scheduledArrival;

    @Column(nullable = false)
    private LocalTime scheduledDeparture;

    @Column(nullable = false)
    private Integer scheduledHaltMinutes;

    @Column(nullable = false)
    private Double distanceFromPrevious;

    public TrainRoute() {
    }

    public TrainRoute(
            Train train,
            Station station,
            Integer sequenceNumber,
            LocalTime scheduledArrival,
            LocalTime scheduledDeparture,
            Integer scheduledHaltMinutes,
            Double distanceFromPrevious
    ) {
        this.train = train;
        this.station = station;
        this.sequenceNumber = sequenceNumber;
        this.scheduledArrival = scheduledArrival;
        this.scheduledDeparture = scheduledDeparture;
        this.scheduledHaltMinutes = scheduledHaltMinutes;
        this.distanceFromPrevious = distanceFromPrevious;
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

    public Integer getSequenceNumber() {
        return sequenceNumber;
    }

    public void setSequenceNumber(Integer sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public LocalTime getScheduledArrival() {
        return scheduledArrival;
    }

    public void setScheduledArrival(LocalTime scheduledArrival) {
        this.scheduledArrival = scheduledArrival;
    }

    public LocalTime getScheduledDeparture() {
        return scheduledDeparture;
    }

    public void setScheduledDeparture(LocalTime scheduledDeparture) {
        this.scheduledDeparture = scheduledDeparture;
    }

    public Integer getScheduledHaltMinutes() {
        return scheduledHaltMinutes;
    }

    public void setScheduledHaltMinutes(Integer scheduledHaltMinutes) {
        this.scheduledHaltMinutes = scheduledHaltMinutes;
    }

    public Double getDistanceFromPrevious() {
        return distanceFromPrevious;
    }

    public void setDistanceFromPrevious(Double distanceFromPrevious) {
        this.distanceFromPrevious = distanceFromPrevious;
    }
}
